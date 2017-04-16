/**
 *
 */
package jp.co.humane.rtc.tool.connector.shell.command.table;

import java.util.LinkedHashMap;

/**
 * ls --connect コマンド実行結果を格納するBean.
 * @author terada.
 *
 */
public class LsConnectBean implements Comparable<LsConnectBean> {

    /** ヘッダ情報 */
    public static final LinkedHashMap<String, Object> HEADER;
    static{
        HEADER = new LinkedHashMap<>();
        HEADER.put("rtcName1",         "RTC-Name");
        HEADER.put("portName1",        "Port-Name");
        HEADER.put("direction",        " ");
        HEADER.put("rtcName2",         "RTC-Name");
        HEADER.put("portName2",        "Port-Name");
        HEADER.put("dataType",         "Data");
        HEADER.put("interfaceType",    "Interface");
        HEADER.put("dataFlowType",     "Data-Flow");
        HEADER.put("subscriptionType", "Subscription-Type");
    }

    /** 接続方向：右から左 */
    public static final String DIRECTION_R2L = "<--";

    /** 接続方向：左から右 */
    public static final String DIRECTION_L2R = "-->";

    /** RTC名1 */
    private String rtcName1 = null;

    /** ポート名1 */
    private String portName1 = null;

    /** 接続方向 */
    private String direction = null;

    /** RTC名2 */
    private String rtcName2 = null;

    /** ポート名2 */
    private String portName2 = null;

    /** データタイプ */
    private String dataType = null;

    /** インタフェースタイプ */
    private String interfaceType = null;

    /** データフロータイプ */
    private String dataFlowType = null;

    /** サブスクリプションタイプ */
    private String subscriptionType = null;

    /**
     * rtcName1を取得する。
     * @return rtcName1 rtcName1。
     */
    public String getRtcName1() {
        return rtcName1;
    }

    /**
     * rtcName1を設定する。
     * @param rtcName1 rtcName1.
     */
    public void setRtcName1(String rtcName1) {
        this.rtcName1 = rtcName1;
    }

    /**
     * portName1を取得する。
     * @return portName1 portName1。
     */
    public String getPortName1() {
        return portName1;
    }

    /**
     * portName1を設定する。
     * @param portName1 portName1.
     */
    public void setPortName1(String portName1) {
        this.portName1 = portName1;
    }

    /**
     * directionを取得する。
     * @return direction direction。
     */
    public String getDirection() {
        return direction;
    }

    /**
     * directionを設定する。
     * @param direction direction.
     */
    public void setDirection(String direction) {
        this.direction = direction;
    }

    /**
     * rtcName2を取得する。
     * @return rtcName2 rtcName2。
     */
    public String getRtcName2() {
        return rtcName2;
    }

    /**
     * rtcName2を設定する。
     * @param rtcName2 rtcName2.
     */
    public void setRtcName2(String rtcName2) {
        this.rtcName2 = rtcName2;
    }

    /**
     * portName2を取得する。
     * @return portName2 portName2。
     */
    public String getPortName2() {
        return portName2;
    }

    /**
     * portName2を設定する。
     * @param portName2 portName2.
     */
    public void setPortName2(String portName2) {
        this.portName2 = portName2;
    }

    /**
     * dataTypeを取得する。
     * @return dataType dataType。
     */
    public String getDataType() {
        return dataType;
    }

    /**
     * dataTypeを設定する。
     * @param dataType dataType.
     */
    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    /**
     * interfaceTypeを取得する。
     * @return interfaceType interfaceType。
     */
    public String getInterfaceType() {
        return interfaceType;
    }

    /**
     * interfaceTypeを設定する。
     * @param interfaceType interfaceType.
     */
    public void setInterfaceType(String interfaceType) {
        this.interfaceType = interfaceType;
    }

    /**
     * dataFlowTypeを取得する。
     * @return dataFlowType dataFlowType。
     */
    public String getDataFlowType() {
        return dataFlowType;
    }

    /**
     * dataFlowTypeを設定する。
     * @param dataFlowType dataFlowType.
     */
    public void setDataFlowType(String dataFlowType) {
        this.dataFlowType = dataFlowType;
    }

    /**
     * subscriptionTypeを取得する。
     * @return subscriptionType subscriptionType。
     */
    public String getSubscriptionType() {
        return subscriptionType;
    }

    /**
     * subscriptionTypeを設定する。
     * @param subscriptionType subscriptionType.
     */
    public void setSubscriptionType(String subscriptionType) {
        this.subscriptionType = subscriptionType;
    }

    /**
     * ソート順番を定義。
     * @inheritDoc
     */
    @Override
    public int compareTo(LsConnectBean o) {
        String key1 = rtcName1 + portName1 + direction + rtcName2 + portName2
                      + dataType + interfaceType + dataFlowType + subscriptionType;
        String key2 = o.getRtcName1() + o.getPortName1() + o.getDirection() + o.getRtcName2() + o.getPortName2()
                      + o.getDataType() + o.getInterfaceType() + o.getDataFlowType() + o.getSubscriptionType();
        return key1.compareTo(key2);
    }

}
