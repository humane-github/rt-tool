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
import org.springframework.stereotype.Component;

import RTC.ConnectorProfile;
import RTC.PortProfile;
import RTC.PortService;
import RTC.RTObject;
import RTC.RTObjectHelper;
import _SDOPackage.ConfigurationSet;
import _SDOPackage.InterfaceNotImplemented;
import _SDOPackage.InternalError;
import _SDOPackage.InvalidParameter;
import _SDOPackage.NameValue;
import _SDOPackage.NotAvailable;
import jp.co.humane.rtc.tool.connector.common.consts.PropertyKey;
import jp.co.humane.rtc.tool.connector.common.enums.ComponentState;
import jp.co.humane.rtc.tool.connector.dao.NamingServerDao;
import jp.co.humane.rtc.tool.connector.dto.ConnectInfo;
import jp.co.humane.rtc.tool.connector.dto.PortInfo;
import jp.co.humane.rtc.tool.connector.dto.RtcInfo;
import jp.go.aist.rtm.RTC.CorbaNaming;
import jp.go.aist.rtm.RTC.util.ORBUtil;

/**
 * ネーミングサービスのデータにアクセスするDAO。
 * @author terada.
 *
 */
@Component
public class NamingServerDaoImpl implements NamingServerDao {

    /** システムプロパティのキー：サーバー名 */
    public static final String SERVER_NAME = "rtc.connector.server.name";

    /** システムプロパティのキー：ポート番号 */
    public static final String PORT_NUMBER = "rtc.connector.port.number";

    /** オブジェクト参照の最大値 */
    private static final int MAX_BINDING_SIZE = 100;

    /** RTCのIDL */
    private static final String RTC_IDL = "IDL:omg.org/RTC/RTObject:1.0";

    /** InPortのポートタイプ */
    private static final String PORT_TYPE_IN = "DataInPort";

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
        String server = System.getProperty(SERVER_NAME);
        String port = System.getProperty(PORT_NUMBER);
        orb = ORBUtil.getOrb();
        corbaNaming = new CorbaNaming(orb, server + ":" + port);
    }

    /**
     * RTC情報の一覧を取得する。
     * @return RTC情報の一覧。
     */
    public List<RtcInfo> getRtcList() {
        return new ArrayList<>(rtcMap.values());
    }

    /**
     * RTC情報を取得する。
     * @param rtcName RTC名。
     * @return RTC情報。
     */
    public RtcInfo getRtcInfo(String rtcName) {
        return rtcMap.get(rtcName);
    }

    /**
     * ポート情報のマップ（key:RTC名、value:ポート一覧）を取得する。
     * @return ポート情報のマップ。
     */
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
            throw new RuntimeException("RTC [" + rtcName + "] は存在しません。");
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
                throw new RuntimeException("RTC [" + rtcName + "] にパラメータ [" + paramName + "] は存在しません。");
            }

            // 設定情報を更新する
            rtObj.get_configuration().set_configuration_set_values(confSet);

        } catch (InvalidParameter | NotAvailable | InternalError | InterfaceNotImplemented ex) {
            throw new RuntimeException("予期せぬエラーが発生しました。\n" + ex.getMessage());
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
            case PropertyKey.DATA_TYPE:
                port.setDataType(val);
                break;

            // データフロータイプはカンマ区切りで要素を格納
            case PropertyKey.DATAFLOW_TYPE:
                Set<String> dataFlowSet = new LinkedHashSet<>();
                for (String type : val.split(",")) {
                    dataFlowSet.add(type.trim());
                }
                port.setDataFlowSet(dataFlowSet);
                break;

            // インタフェースタイプは文字列を格納
            case PropertyKey.INTERFACE_TYPE:
                port.setInterfaceType(val);
                break;

            // ポートタイプは"DataInPort"の場合にInPort指定する
            case PropertyKey.PORT_TYPE:
                if (PORT_TYPE_IN.equals(val)) {
                    port.setIsInPort(true);
                } else {
                    port.setIsInPort(false);
                }
                break;

            // サブスクリプションタイプはカンマ区切りで要素を格納
            case PropertyKey.SUBSCRIPTION_TYPE:
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
                con.setInRtcName(inPort.owner.get_sdo_id());
                con.setOutRtcName(outPort.owner.get_sdo_id());
            } catch (NotAvailable | InternalError ex) {
                throw new RuntimeException(ex);
            }

            // プロファイル情報
            for (NameValue nv : prof.properties) {
                String key = nv.name;
                String val = null;

                switch(key) {

                case PropertyKey.DATA_TYPE:
                    val = nv.value.extract_string();
                    con.setDataType(val);
                    break;

                case PropertyKey.DATAFLOW_TYPE:
                    val = nv.value.extract_string();
                    con.setDataFlowType(val);
                    break;

                case PropertyKey.INTERFACE_TYPE:
                    val = nv.value.extract_string();
                    con.setInterfaceType(val);
                    break;

                case PropertyKey.SUBSCRIPTION_TYPE:
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
