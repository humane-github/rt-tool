/**
 *
 */
package jp.co.humane.rtc.tool.connector.dto;

/**
 * 接続情報を格納するBean.
 * @author terada.
 *
 */
public class ConnectInfo {

    /** 接続ID */
    private String id = null;

    /** 接続名 */
    private String name = null;

    /** OutRTC名 */
    private String outRtcName = null;

    /** InRTC名 */
    private String inRtcName = null;

    /** Outポート名 */
    private String inPortName = null;

    /** Inポート名 */
    private String outPortName = null;

    /** インタフェースタイプ */
    private String interfaceType = null;

    /** データフロータイプ */
    private String dataFlowType = null;

    /** サブスクリプションタイプ */
    private String subscriptionType = null;

    /** データタイプ */
    private String dataType = null;

    /**
     * idを取得する。
     * @return id id。
     */
    public String getId() {
        return id;
    }

    /**
     * idを設定する。
     * @param id id.
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * nameを取得する。
     * @return name name。
     */
    public String getName() {
        return name;
    }

    /**
     * nameを設定する。
     * @param name name.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * outRtcNameを取得する。
     * @return outRtcName outRtcName。
     */
    public String getOutRtcName() {
        return outRtcName;
    }

    /**
     * outRtcNameを設定する。
     * @param outRtcName outRtcName.
     */
    public void setOutRtcName(String outRtcName) {
        this.outRtcName = outRtcName;
    }

    /**
     * inRtcNameを取得する。
     * @return inRtcName inRtcName。
     */
    public String getInRtcName() {
        return inRtcName;
    }

    /**
     * inRtcNameを設定する。
     * @param inRtcName inRtcName.
     */
    public void setInRtcName(String inRtcName) {
        this.inRtcName = inRtcName;
    }

    /**
     * inPortNameを取得する。
     * @return inPortName inPortName。
     */
    public String getInPortName() {
        return inPortName;
    }

    /**
     * inPortNameを設定する。
     * @param inPortName inPortName.
     */
    public void setInPortName(String inPortName) {
        this.inPortName = inPortName;
    }

    /**
     * outPortNameを取得する。
     * @return outPortName outPortName。
     */
    public String getOutPortName() {
        return outPortName;
    }

    /**
     * outPortNameを設定する。
     * @param outPortName outPortName.
     */
    public void setOutPortName(String outPortName) {
        this.outPortName = outPortName;
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
}
