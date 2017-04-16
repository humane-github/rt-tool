/**
 *
 */
package jp.co.humane.rtc.tool.connector.shell.command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.CommandMarker;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.stereotype.Component;

import jp.co.humane.rtc.tool.connector.dao.NamingServerDao;
import jp.co.humane.rtc.tool.connector.dto.ConnectInfo;
import jp.co.humane.rtc.tool.connector.dto.PortInfo;
import jp.co.humane.rtc.tool.connector.dto.RtcInfo;
import jp.co.humane.rtc.tool.connector.shell.command.arg.RtcParam;
import jp.co.humane.rtc.tool.connector.shell.command.table.LsBean;
import jp.co.humane.rtc.tool.connector.shell.command.table.LsConfigBean;
import jp.co.humane.rtc.tool.connector.shell.command.table.LsConnectBean;
import jp.co.humane.rtc.tool.connector.shell.command.table.LsPortBean;
import jp.co.humane.rtc.tool.connector.shell.common.TableBuilderUtil;

/**
 * lsコマンドの処理を定義。
 * @author terada.
 *
 */
@Component
public class LsCommand implements CommandMarker {

    /** リストの種類：RTC */
    private static final int TYPE_RTC = 0;

    /** リストの種類：ポート */
    private static final int TYPE_PORT = 1;

    /** リストの種類：接続 */
    private static final int TYPE_CONNECT = 2;

    /** リストの種類：設定 */
    private static final int TYPE_CONFIG = 3;

    /** ネーミングサーバーDAO */
    @Autowired
    private NamingServerDao dao = null;

    @CliCommand(value = "ls", help = "Show RTC info list")
    public String simple(
            @CliOption(key = { "port" }, mandatory = false, help = "Show RTC port list", specifiedDefaultValue = "") final RtcParam portParam,
            @CliOption(key = { "connect" }, mandatory = false, help = "Show RTC connection list", specifiedDefaultValue = "") final RtcParam conParam,
            @CliOption(key = { "config" }, mandatory = false, help = "Show RTC configuration list", specifiedDefaultValue = "") final RtcParam cfgParam
            ) {

        dao.reflesh();
        String message = null;
        int type = decideListType(portParam, conParam, cfgParam);
        switch (type) {
        case TYPE_RTC:
            message = getLsResponse();
            break;

        case TYPE_PORT:
            message = getLsPortResponse(portParam.getName());
            break;

        case TYPE_CONNECT:
            message = getLsConnectResponse(conParam.getName());
            break;

        case TYPE_CONFIG:
            message = getLsConfigResponse(cfgParam.getName());
            break;

        }
        return message;

    }


    private int decideListType(RtcParam portParam, RtcParam conParam, RtcParam cfgParam) {

        if (null != portParam) {
            return TYPE_PORT;
        }

        if (null != conParam) {
            return TYPE_CONNECT;
        }

        if (null != cfgParam) {
            return TYPE_CONFIG;
        }

        return TYPE_RTC;


    }

    /**
     * RTCのリストからlsコマンドの結果を取得する。
     * @return lsコマンドの結果。
     */
    private String getLsResponse() {

        List<RtcInfo> rtcList = dao.getRtcList();

        // lsコマンド出力リストを取得
        List<LsBean> beanList = new ArrayList<>();
        for (RtcInfo rtc : rtcList) {
            LsBean bean = new LsBean();
            bean.setDirectory(rtc.getDirectory());
            bean.setId(rtc.getId());
            bean.setKind(rtc.getKind());
            bean.setState(rtc.getState().name());
            beanList.add(bean);
        }

        // リストをテーブル表現の文字列にして返却
        Collections.sort(beanList);
        return TableBuilderUtil.render(beanList, LsBean.HEADER);
    }

    /**
     * ls --portのコマンド結果を取得する。
     * @param name 出力対象のRTC名。
     * @return ls --portコマンドの結果。
     */
    private String getLsPortResponse(String name) {

        List<LsPortBean> beanList = new ArrayList<>();

        // ポート一覧からコマンド結果のBeanリストを生成
        Map<String, List<PortInfo>> portMap = dao.getPortMap();
        for (String rtcName: portMap.keySet()) {

            // RTC名の指定があり、処理対象と一致しない場合は次にスキップ
            if (0 != name.length() && !name.equals(rtcName)) {
                continue;
            }

            for (PortInfo port : portMap.get(rtcName)) {
                LsPortBean bean = new LsPortBean();
                bean.setRtcName(rtcName);
                bean.setName(port.getName());
                bean.setPortType(port.isInPort() ? "InPort" : "OutPort");
                bean.setConnectNum(String.valueOf(dao.getConnectNum(rtcName, port.getName())));
                bean.setDataType(port.getDataType());
                bean.setSubscriptionTypeSet(joinSet(port.getSubscriptionTypeSet()));
                bean.setDataFlowSet(joinSet(port.getDataFlowSet()));
                bean.setInterfaceType(port.getInterfaceType());
                beanList.add(bean);
            }
        }

        // Beanリストをテーブル形式に変換
       Collections.sort(beanList);
       return TableBuilderUtil.render(beanList, LsPortBean.HEADER);
    }

    /**
     * ls --connectのコマンド結果を取得する。
     * @param name 出力対象のRTC名。
     * @return ls --connectコマンドの結果。
     */
    private String getLsConnectResponse(String name) {

        // 指定がある場合はポートに対応した接続情報を出力
        if (!name.isEmpty()) {
            return getLsConnectResponseByRtcName(name);
        }

        // 接続一覧からコマンド結果のBeanリストを生成
        List<LsConnectBean> beanList = new ArrayList<>();
        List<ConnectInfo> conList = dao.getConnectInfoList();
        for (ConnectInfo con : conList) {
            LsConnectBean bean = new LsConnectBean();
            bean.setRtcName1(con.getOutRtcName());
            bean.setPortName1(con.getOutPortName());
            bean.setDirection(LsConnectBean.DIRECTION_L2R);
            bean.setRtcName2(con.getInRtcName());
            bean.setPortName2(con.getInPortName());
            bean.setDataType(con.getDataType());
            bean.setInterfaceType(con.getInterfaceType());
            bean.setDataFlowType(con.getDataFlowType());
            bean.setSubscriptionType(con.getSubscriptionType());
            beanList.add(bean);
        }

        // Beanリストをテーブル形式に変換
        Collections.sort(beanList);
        return TableBuilderUtil.render(beanList, LsConnectBean.HEADER);

    }

    /**
     * RTC名に対応した接続情報を取得する。
     * @param rtcName RTC名。
     * @return RTC名に対応した接続情報。
     */
    private String getLsConnectResponseByRtcName(String rtcName) {

        // RTC名に対応するRTC情報を取得
        RtcInfo rtc = dao.getRtcInfo(rtcName);
        if (null == rtc) {
            return rtcName + "is not registerd.";
        }

        // ポートごとに接続情報を取得する
        List<LsConnectBean> beanList = new ArrayList<>();
        for (String portName : rtc.getPortNameList()) {

            // 接続情報がない場合でもポート情報を設定
            if (0 == rtc.getConnectMap().get(portName).size()) {
                LsConnectBean bean = new LsConnectBean();
                bean.setRtcName1(rtcName);
                bean.setPortName1(portName);
                beanList.add(bean);
                continue;
            }

            // ポートに対する接続の数だけ接続情報を設定
            for (ConnectInfo con : rtc.getConnectMap().get(portName)) {

                LsConnectBean bean = new LsConnectBean();
                bean.setRtcName1(rtcName);
                bean.setPortName1(portName);
                bean.setDirection(LsConnectBean.DIRECTION_L2R);
                bean.setRtcName2(con.getInRtcName());
                bean.setPortName2(con.getInPortName());
                bean.setDataType(con.getDataType());
                bean.setInterfaceType(con.getInterfaceType());
                bean.setDataFlowType(con.getDataFlowType());
                bean.setSubscriptionType(con.getSubscriptionType());

                if (rtcName.equals(con.getInRtcName())) {
                    bean.setDirection(LsConnectBean.DIRECTION_R2L);
                    bean.setRtcName2(con.getOutRtcName());
                    bean.setPortName2(con.getOutPortName());
                }
                beanList.add(bean);
            }
        }

        // Beanリストをテーブル形式に変換
        Collections.sort(beanList);
        return TableBuilderUtil.render(beanList, LsConnectBean.HEADER);
    }


    /**
     * ls --configのコマンド結果を取得する。
     * @param name 出力対象のRTC名。
     * @return ls --portコマンドの結果。
     */
    private String getLsConfigResponse(String name) {

        List<LsConfigBean> beanList = new ArrayList<>();

        // 設定マップからコマンド結果のBeanリストを生成
        Map<String, Map<String, String>> configMap = dao.getConfigMap();
        for (String rtcName: configMap.keySet()) {

            // RTC名の指定があり、処理対象と一致しない場合は次にスキップ
            if (0 != name.length() && !name.equals(rtcName)) {
                continue;
            }

            // 設定情報をリストに格納
            for (Entry<String, String> entry : configMap.get(rtcName).entrySet()) {
                LsConfigBean bean = new LsConfigBean();
                bean.setRtcName(rtcName);
                bean.setParamName(entry.getKey());
                bean.setParamValue(entry.getValue());
                beanList.add(bean);
            }
        }

        // Beanリストをテーブル形式に変換
        Collections.sort(beanList);
        return TableBuilderUtil.render(beanList, LsConfigBean.HEADER);
    }

    /**
     * Setの情報を"/"区切りで連結する。
     * @param set Set情報。
     * @return 連結後の文字列。
     */
    private String joinSet(Set<String> set) {
        StringBuilder sb = new StringBuilder();
        for (String s: set) {
            sb.append(s + "/");
        }
        sb.setLength(sb.length() - 1);
        return sb.toString();
    }


}
