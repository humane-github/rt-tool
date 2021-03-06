/**
 *
 */
package jp.co.humane.rtc.tool.connector.dao.impl;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

import org.omg.CORBA.ORB;
import org.omg.CORBA.SystemException;
import org.omg.CosNaming.Binding;
import org.omg.CosNaming.BindingIteratorHolder;
import org.omg.CosNaming.BindingListHolder;
import org.omg.CosNaming.BindingType;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContext;
import org.omg.CosNaming.NamingContextHelper;
import org.omg.CosNaming.NamingContextPackage.CannotProceed;
import org.omg.CosNaming.NamingContextPackage.InvalidName;
import org.omg.CosNaming.NamingContextPackage.NotFound;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import RTC.ConnectorProfile;
import RTC.ConnectorProfileHolder;
import RTC.PortProfile;
import RTC.PortService;
import RTC.RTObject;
import RTC.RTObjectHelper;
import RTC.ReturnCode_t;
import _SDOPackage.ConfigurationSet;
import _SDOPackage.InterfaceNotImplemented;
import _SDOPackage.InternalError;
import _SDOPackage.InvalidParameter;
import _SDOPackage.NameValue;
import _SDOPackage.NotAvailable;
import jp.co.humane.rtc.tool.connector.common.consts.RtmPropertyKey;
import jp.co.humane.rtc.tool.connector.common.consts.SystemPropertyKey;
import jp.co.humane.rtc.tool.connector.common.enums.ComponentState;
import jp.co.humane.rtc.tool.connector.common.exceptions.ApplicationException;
import jp.co.humane.rtc.tool.connector.component.ConnectProfileHolder;
import jp.co.humane.rtc.tool.connector.dao.NamingServerDao;
import jp.co.humane.rtc.tool.connector.dto.ConnectInfo;
import jp.co.humane.rtc.tool.connector.dto.PortInfo;
import jp.co.humane.rtc.tool.connector.dto.RtcInfo;
import jp.go.aist.rtm.RTC.CorbaNaming;
import jp.go.aist.rtm.RTC.util.NVUtil;
import jp.go.aist.rtm.RTC.util.ORBUtil;

/**
 * ネーミングサービスのデータにアクセスするDAO。
 * @author terada.
 *
 */
@Component
public class NamingServerDaoImpl implements NamingServerDao {

    /** オブジェクト参照の最大値 */
    private static final int MAX_BINDING_SIZE = 100;

    /** RTCのIDL */
    private static final String RTC_IDL = "IDL:omg.org/RTC/RTObject:1.0";

    /** InPortのポートタイプ */
    private static final String PORT_TYPE_IN = "DataInPort";

    /** ロガー */
    private static final Logger LOGGER = LoggerFactory.getLogger(NamingServerDaoImpl.class);

    /** 接続設定 */
    @Autowired
    private ConnectProfileHolder connectProfileHolder = null;

    /** orb */
    private ORB orb = null;

    /** ネーミングサービス */
    private CorbaNaming corbaNaming = null;

    /** オブジェクト参照のマップ(key:コンポーネント名、value:RTC情報) */
    private Map<String, RtcInfo> rtcMap = new LinkedHashMap<>();

    /**
     * コンストラクタ。
     * @throws Exception 接続に失敗した場合に発生。
     */
    public NamingServerDaoImpl() throws Exception {
        String server = System.getProperty(SystemPropertyKey.SERVER_NAME);
        String port = System.getProperty(SystemPropertyKey.PORT_NUMBER);
        orb = ORBUtil.getOrb();
        corbaNaming = new CorbaNaming(orb, server + ":" + port);
    }

    /**
     * RTC情報の一覧を取得する。
     * @return RTC情報の一覧。
     */
    @Override
    public List<RtcInfo> getRtcList() {
        return new ArrayList<>(rtcMap.values());
    }

    /**
     * RTC情報を取得する。
     * @param rtcName RTC名。
     * @return RTC情報。
     */
    @Override
    public RtcInfo getRtcInfo(String rtcName) {
        return rtcMap.get(rtcName);
    }

    /**
     * ポート情報のマップ（key:RTC名、value:ポート一覧）を取得する。
     * @return ポート情報のマップ。
     */
    @Override
    public Map<String, List<PortInfo>> getPortMap() {

        Map<String, List<PortInfo>> portMap = new LinkedHashMap<>();
        for (String rtcName : rtcMap.keySet()) {
            RtcInfo rtc = rtcMap.get(rtcName);
            List<PortInfo> portList = new ArrayList<>(rtc.getPortMap().values());
            portMap.put(rtcName, portList);
        }
        return portMap;
    }

    /**
     * ポートの接続数を取得する。
     * @param rtcName   RTC名。
     * @param portName  ポート名。
     * @return 接続数。
     */
    @Override
    public int getConnectNum(String rtcName, String portName) {

        // 指定されたRTCが存在しない場合は0を返す
        RtcInfo rtc = rtcMap.get(rtcName);
        if (null == rtc) {
            return 0;
        }

        // 対象ポートが存在しない場合は0を返す
        Map<String, List<ConnectInfo>> connectMap = rtc.getConnectMap();
        List<ConnectInfo> connectList = connectMap.get(portName);
        if (null == connectList) {
            return 0;
        }

        // 接続数を返す
        return connectList.size();
    }

    /**
     * 接続情報のリストを取得する。
     * @return 接続情報のリスト。
     */
    @Override
    public List<ConnectInfo> getConnectInfoList() {

        // 2重にカウントしないようにするためInPort側の接続情報だけ返す
        List<ConnectInfo> ret = new ArrayList<>();
        for (String rtcName : rtcMap.keySet()) {
            RtcInfo rtc = rtcMap.get(rtcName);
            for (List<ConnectInfo> conList :  rtc.getConnectMap().values()) {
                for (ConnectInfo con : conList) {
                    if (rtcName.equals(con.getInRtcName())) {
                        ret.add(con);
                    }
                }
            }
        }
        return ret;
    }

    /**
     * 設定情報のマップ（key:RTC名、value:設定マップ）を取得する。
     * @return 設定情報のマップ。
     */
    public Map<String, Map<String, String>> getConfigMap() {

        Map<String, Map<String, String>> configMap = new LinkedHashMap<>();
        for (String rtcName : rtcMap.keySet()) {
            RtcInfo rtc = rtcMap.get(rtcName);
            configMap.put(rtcName, rtc.getConfigMap());
        }
        return configMap;
    }

    /**
     * 設定情報を更新する。
     * @param rtcName    RTC名。
     * @param paramName  パラメータ名。
     * @param value      値。
     * @return 成功時はtrue。
     */
    public void updateConfig(String rtcName, String paramName, String value) {

        // 指定RTC名が存在しない場合は例外とする
        if (!rtcMap.containsKey(rtcName)) {
            throw new ApplicationException("RTC [" + rtcName + "] は存在しません。");
        }

        try {
            // 指定パラメータの値を更新する
            RtcInfo rtc = rtcMap.get(rtcName);
            RTObject rtObj = rtc.getObjRef();
            ConfigurationSet confSet = rtObj.get_configuration().get_active_configuration_set();
            boolean isUpdated = false;
            for (NameValue nv : confSet.configuration_data) {
                if (Objects.equals(paramName, nv.name)) {
                    isUpdated = true;
                    nv.value.insert_string(value);
                    break;
                }
            }

            // 指定パラメータが存在しない場合は例外とする
            if (!isUpdated) {
                throw new ApplicationException("RTC [" + rtcName + "] にパラメータ [" + paramName + "] は存在しません。");
            }

            // 設定情報を更新する
            rtObj.get_configuration().set_configuration_set_values(confSet);

        } catch (InvalidParameter | NotAvailable | InternalError | InterfaceNotImplemented ex) {
            LOGGER.error("予期せぬエラーが発生しました。", ex);
            throw new RuntimeException("予期せぬエラーが発生しました。");
        }
    }

    /**
     * 2つのポートを接続する。
     * @param outRtcName  出力ポートを持つRTC名。
     * @param outPortName 出力ポート名。
     * @param inRtcName   入力ポートを持つRTC名。
     * @param inPortName  入力ポート名。
     */
    public void connect(String outRtcName, String outPortName, String inRtcName, String inPortName) {

        // RTCが存在しない場合は処理中断
        RTObject obj1 = getRTObject(outRtcName);
        RTObject obj2 = getRTObject(inRtcName);
        if (null == obj1 || null == obj2) {
            throw new ApplicationException("接続対象のRTCが見つかりませんでした。");
        }

        // ポートが見つからない場合は処理中断
        PortService port1 = null;
        PortService port2 = null;
        for(PortService ps : obj1.get_ports()) {
            if (ps.get_port_profile().name.equals(outPortName)) {
                port1 = ps;
                break;
            }
        }
        for (PortService ps : obj2.get_ports()) {
            if (ps.get_port_profile().name.equals(inPortName)) {
                port2 = ps;
                break;
            }
        }
        if (null == port1 || null == port2) {
            throw new ApplicationException("接続対象のポートが見つかりませんでした。");
        }

        // 接続設定を作成
        ConnectorProfile prof = new ConnectorProfile();
        prof.connector_id = outRtcName + ":" + outPortName + "_" + inRtcName + ":" + inPortName;
        prof.name = prof.connector_id;
        prof.ports = new PortService[] {port1, port2};
        List<NameValue> nvList = new ArrayList<>();
        connectProfileHolder.getProfiles().forEach((key, value) -> {
            nvList.add(NVUtil.newNV(key, value));
        });
        prof.properties = nvList.toArray(new NameValue[nvList.size()]);
        /**
        prof.properties = new NameValue[] {
                NVUtil.newNV("dataport.interface_type",    "corba_cdr"),
                NVUtil.newNV("dataport.dataflow_type",     "push"),
                NVUtil.newNV("dataport.subscription_type", "new")
        };
        */

        // 接続を実施
        ReturnCode_t ret = port1.connect(new ConnectorProfileHolder(prof));
        if (ret != ReturnCode_t.RTC_OK) {
            throw new ApplicationException("接続に失敗しました：" + ret);
        }
    }

    /**
     * 2つのポートを切断する。
     * @param outRtcName  OutPortのRTC名。
     * @param outPortName OutPortのポート名。
     * @param inRtcName   InPortのRTC名。
     * @param inPortName  InPortのポート名。
     */
    public void disconnect(String outRtcName, String outPortName, String inRtcName, String inPortName) {

        // InPortのRTCを取得
        RtcInfo outRtc = rtcMap.get(outRtcName);
        if (outRtc == null) {
            throw new ApplicationException("RTC " + outRtcName + " が見つかりませんでした。");
        }

        // InPortのポートを取得
        Map<String, List<ConnectInfo>> connectMap = outRtc.getConnectMap();
        if (!connectMap.containsKey(outPortName)) {
            throw new ApplicationException(outRtcName + "の" + outPortName + "に関する接続情報が見つかりませんでした。");
        }

        // 指定された値に対応する接続IDを取得
        String connectId = null;
        List<ConnectInfo> connectList = connectMap.get(outPortName);
        for (ConnectInfo con : connectList) {
            if (inRtcName.equals(con.getInRtcName()) && inPortName.equals(con.getInPortName())) {
                connectId = con.getId();
            }
        }
        if (connectId == null) {
            throw new ApplicationException("指定の接続は見つかりませんでした。");
        }

        // RTCが存在しない場合は処理中断
        RTObject outObj = getRTObject(outRtcName);
        if (null == outObj) {
            throw new ApplicationException("切断対象のRTCが見つかりませんでした。");
        }

        // ポートが見つからない場合は処理中断
        PortService outPort = null;
        for(PortService ps : outObj.get_ports()) {
            if (ps.get_port_profile().name.equals(outPortName)) {
                outPort = ps;
                break;
            }
        }
        if (null == outPort) {
            throw new ApplicationException("切断対象のポートが見つかりませんでした。");
        }

        // 接続を切断
        outPort.disconnect(connectId);
    }

    /**
     * RTCをアクティブ化する。
     * @param rtcName RTC名。
     */
    public void activate(String rtcName) {

        // 現在のステータスがINACTIVEではない場合は処理中断
        RtcInfo rtcInfo = rtcMap.get(rtcName);
        ComponentState currentState = rtcInfo.getState();
        if (currentState != ComponentState.INACTIVE) {
            throw new ApplicationException(rtcName + "のステータスを" + currentState.toString() + "からACTIVEに変更できません。");
        }

        // RTCを取得
        RTObject rtcObj = getRTObject(rtcName);
        if (null == rtcObj) {
            throw new ApplicationException("更新対象のRTCが見つかりませんでした。");
        }

        // アクティブ化する。
        ReturnCode_t ret = rtcObj.get_owned_contexts()[0].activate_component(rtcObj);
        if (ret != ReturnCode_t.RTC_OK) {
            throw new ApplicationException("アクティブ化の処理に失敗しました。：" + ret.toString());
        }
    }

    /**
     * オブジェクト参照のマップを更新する。
     */
    public void reflesh() {

        // 取得済みの情報があれば解放する
        if (null != rtcMap) {
            for (RtcInfo rtc : rtcMap.values()) {
                rtc.getObjRef()._release();
            }
        }

        // 最新のマップを取得する
        rtcMap = geRtcMap();

        // それぞれの情報に対する詳細情報を取得
        for (RtcInfo rtc : rtcMap.values()) {
            updateDetailInfo(rtc);
        }
    }

    /**
     * RTCの詳細情報を更新する。
     * @param rtc RTC情報。
     */
    protected void updateDetailInfo(RtcInfo rtc) {

        // ポート名一覧、接続情報を取得
        List<String> portNameList = rtc.getPortNameList();

        // 状態の取得
        RTObject obj = rtc.getObjRef();
        int state = obj.get_owned_contexts()[0].get_component_state(obj).value();
        rtc.setState(ComponentState.get(state));

        // ポートの情報
        for(PortService ps : obj.get_ports()) {

            // ポート名を取得
            PortProfile pprof = ps.get_port_profile();
            String portName = pprof.name;
            portNameList.add(portName);

            // ポート情報、接続情報を取得
            PortInfo portInfo = getPortInfo(portName, pprof.properties);
            List<ConnectInfo> connectInfotList = getConnectInfoList(pprof.connector_profiles);

            // RTCにポートの情報を追加
            rtc.getPortMap().put(portName, portInfo);
            rtc.getConnectMap().put(portName, connectInfotList);
        }

        // コンフィグ情報
        Map<String, String> configMap = getConfigMap(obj);
        rtc.setConfigMap(configMap);
    }

    /**
     * ポート情報を取得する。
     * @param props プロパティ情報。
     */
    protected PortInfo getPortInfo(String name, NameValue[] props) {

        PortInfo port = new PortInfo();
        port.setName(name);

        for (NameValue nv : props) {
            String key = nv.name;
            String val = nv.value.extract_string();

            switch(key) {

            // データタイプは文字列を格納
            case RtmPropertyKey.DATA_TYPE:
                port.setDataType(val);
                break;

            // データフロータイプはカンマ区切りで要素を格納
            case RtmPropertyKey.DATAFLOW_TYPE:
                Set<String> dataFlowSet = new LinkedHashSet<>();
                for (String type : val.split(",")) {
                    dataFlowSet.add(type.trim());
                }
                port.setDataFlowSet(dataFlowSet);
                break;

            // インタフェースタイプは文字列を格納
            case RtmPropertyKey.INTERFACE_TYPE:
                port.setInterfaceType(val);
                break;

            // ポートタイプは"DataInPort"の場合にInPort指定する
            case RtmPropertyKey.PORT_TYPE:
                if (PORT_TYPE_IN.equals(val)) {
                    port.setIsInPort(true);
                } else {
                    port.setIsInPort(false);
                }
                break;

            // サブスクリプションタイプはカンマ区切りで要素を格納
            case RtmPropertyKey.SUBSCRIPTION_TYPE:
                Set<String> subTypeSet = new LinkedHashSet<>();
                for (String type : val.split(",")) {
                    subTypeSet.add(type.trim());
                }
                port.setSubscriptionTypeSet(subTypeSet);
                break;

            default:
                // それ以外のプロパティは格納しない
            }
        }

        return port;
    }

    /**
     * 接続情報のリストを取得する。
     * @param profiles 接続情報プロファイル配列。
     * @return 接続情報リスト。
     */
    protected List<ConnectInfo> getConnectInfoList(ConnectorProfile[] profiles) {

        List<ConnectInfo> connectInfoList = new ArrayList<>();

        // 接続数だけループ
        for (ConnectorProfile prof : profiles) {

            // 接続情報を取得
            ConnectInfo con = new ConnectInfo();
            con.setId(prof.connector_id);
            con.setName(prof.name);

            // inポート、outポートを設定
            PortProfile inPort = prof.ports[0].get_port_profile();
            PortProfile outPort = prof.ports[1].get_port_profile();
            if (!getPortInfo(inPort.name, inPort.properties).isInPort()) {
                PortProfile tmp = inPort;
                inPort = outPort;
                outPort = tmp;
            }
            con.setInPortName(inPort.name);
            con.setOutPortName(outPort.name);
            try {
                con.setInRtcName(inPort.owner.get_sdo_type());
                con.setOutRtcName(outPort.owner.get_sdo_type());
            } catch (NotAvailable | InternalError ex) {
                throw new RuntimeException(ex);
            }

            // プロファイル情報
            for (NameValue nv : prof.properties) {
                String key = nv.name;
                String val = null;

                switch(key) {

                case RtmPropertyKey.DATA_TYPE:
                    val = nv.value.extract_string();
                    con.setDataType(val);
                    break;

                case RtmPropertyKey.DATAFLOW_TYPE:
                    val = nv.value.extract_string();
                    con.setDataFlowType(val);
                    break;

                case RtmPropertyKey.INTERFACE_TYPE:
                    val = nv.value.extract_string();
                    con.setInterfaceType(val);
                    break;

                case RtmPropertyKey.SUBSCRIPTION_TYPE:
                    val = nv.value.extract_string();
                    con.setSubscriptionType(val);
                    break;

                default:
                    // それ以外は取得しない
                }
            }

            // 接続情報をリストに追加
            connectInfoList.add(con);
        }

        return connectInfoList;
    }

    /**
     * RTC情報からコンフィグ情報を取得する。
     * @param obj RTCオブジェクト参照。
     * @return コンフィグ情報。
     */
    protected Map<String, String> getConfigMap(RTObject obj) {

        Map<String, String> configMap = new LinkedHashMap<>();

        // オブジェクト参照からアクティブなコンフィグ情報を取得
        NameValue[] configArr = null;
        try {
            configArr = obj.get_configuration().get_active_configuration_set().configuration_data;
        } catch (NotAvailable | InternalError | InterfaceNotImplemented e) {
            return configMap;
        }

        // マップに格納しなおして返す
        for (NameValue nv : configArr) {
            String key = nv.name;
            String val = nv.value.extract_string();
            configMap.put(key, val);
        }

        return configMap;
    }

    /**
     * すべてのRTCのオブジェクト参照をマップとして取得する。
     * @return RTCオブジェクト参照マップ。
     */
    protected Map<String, RtcInfo> geRtcMap() {

        Map<String, RtcInfo> map = new TreeMap<>();

        // 登録されているオブジェクトの一覧を取得
        for (Binding b: getBindings(corbaNaming)) {
            recursiveGetObjRef("", b, map);
        }
        return map;
    }

    /**
     * 再帰的にオブジェクト参照を取得し、マップに登録する。
     * @param directory ディレクトリ。
     * @param binding   バインド情報。
     * @param map       マップ。
     */
    protected void recursiveGetObjRef(String directory, Binding binding, Map<String, RtcInfo> map) {

        // 対象としているオブジェクト参照のidとkindからパスを取得
        NameComponent nc = binding.binding_name[0];
        String path = directory + nc.id + "." + nc.kind;

        // オブジェクト参照を取得
        org.omg.CORBA.Object obj = null;
        try {
            obj = corbaNaming.resolve(path);
        } catch (InvalidName | SystemException | NotFound | CannotProceed ex) {
            return;
        }

        // ネーミングコンテキストの場合は配下の情報を取得
        if (BindingType.ncontext == binding.binding_type) {

            NamingContext context = NamingContextHelper.narrow(obj);
            for (Binding b : getBindings(context)) {
                recursiveGetObjRef(path + "/", b, map);
            }
            return;
        }

        // 存在しない場合(前回の削除漏れ)はアンバインド
        if (!isExist(obj)) {
            try {
                corbaNaming.unbind(path);
            } catch (SystemException | NotFound | CannotProceed | InvalidName ex) {
                ;
            }
            obj._release();
            return;
        }

        // オブジェクト参照がRTCではない場合は取得対象外
        if (!obj._is_a(RTC_IDL)) {
            obj._release();
            return;
        }

        // マップに追加
        RTObject rtc = RTObjectHelper.narrow(obj);
        map.put(nc.id, new RtcInfo(directory, rtc, nc.id, nc.kind));
    }

    /**
     * オブジェクト参照が存在しているか確認する。
     * @param obj オブジェクト参照。
     * @return 存在している場合はtrue。
     */
    protected boolean isExist(org.omg.CORBA.Object obj) {

        boolean isExist = false;

        try {
            isExist = !obj._non_existent();
        } catch (RuntimeException ex) {
            // 接続できない情報は存在していないとみなす
            isExist = false;
        }

        return isExist;
    }


    /**
     * CorbaNamingからバインド情報を取得する。
     * @param naming CorbaNamin.
     * @return バインド情報。
     */
    protected Binding[] getBindings(CorbaNaming naming) {
        BindingListHolder bindings = new BindingListHolder();
        BindingIteratorHolder itr = new BindingIteratorHolder();
        naming.list(naming.getRootContext(), MAX_BINDING_SIZE, bindings, itr);
        return bindings.value;
    }

    /**
     * NamingContextgからバインド情報を取得する。
     * @param naming NamingContext.
     * @return バインド情報。
     */
    protected Binding[] getBindings(NamingContext naming) {
        BindingListHolder bindings = new BindingListHolder();
        BindingIteratorHolder itr = new BindingIteratorHolder();
        naming.list(MAX_BINDING_SIZE, bindings, itr);
        return bindings.value;
    }


    /**
     * 名前(id)に対応するオブジェクト参照を返す。
     * @param name 名前(id)
     * @return オブジェクト参照。
     */
    protected RTObject getRTObject(String name) {

        // マップが無いまたはマップに名前が存在しない場合は更新を行う
        if (null == rtcMap || !rtcMap.containsKey(name)) {
            reflesh();
        }

        // マップから対応するオブジェクト参照を返す
        RtcInfo orh = rtcMap.get(name);
        if (null != orh) {
            return orh.getObjRef();
        }

        // 存在しない場合はnullを返す
        return null;
    }
}