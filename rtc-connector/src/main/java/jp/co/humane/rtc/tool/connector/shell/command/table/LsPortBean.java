/**
 *
 */
package jp.co.humane.rtc.tool.connector.shell.command.table;

import java.util.LinkedHashMap;

/**
 * ls --port コマンド実行結果を格納するBean.
 * @author terada.
 *
 */
public class LsPortBean implements Comparable<LsPortBean> {

    /** ヘッダ情報 */
    public static final LinkedHashMap<String, Object> HEADER;
    static{
        HEADER = new LinkedHashMap<>();
        HEADER.put("rtcName",             "RTC-Name");
        HEADER.put("name",                "Port-Name");
        HEADER.put("connectNum",          "Connect");
        HEADER.put("portType",            "In/Out");
        HEADER.put("dataType",            "Data");
        HEADER.put("subscriptionTypeSet", "Subscription-Type");
        HEADER.put("dataFlowSet",         "Data-Flow");
        HEADER.put("interfaceType",       "Interface");
    }

    /** RTC名 */
    private String rtcName = "";

    /** ポート名 */
    private String name = "";

    /** 接続数 */
    private String connectNum = "";

    /** ポートの種類 */
    private String portType = "";

    /** データタイプ */
    private String dataType = "";

    /** 利用可能なサブスクリプションタイプセット */
    private String subscriptionTypeSet = "";

    /** 利用可能なデータフロータイプセット */
    private String dataFlowSet = "";

    /** インターフェースタイプ */
    private String interfaceType = "";

    /**
     * rtcNameを取得する。
     * @return rtcName rtcName。
     */
    public String getRtcName() {
        return rtcName;
    }

    /**
     * rtcNameを設定する。
     * @param rtcName rtcName.
     */
    public void setRtcName(String rtcName) {
        this.rtcName = rtcName;
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
     * portTypeを取得する。
     * @return portType portType。
     */
    public String getPortType() {
        return portType;
    }

    /**
     * portTypeを設定する。
     * @param portType portType.
     */
    public void setPortType(String portType) {
        this.portType = portType;
    }

    /**
     * connectNumを取得する。
     * @return connectNum connectNum。
     */
    public String getConnectNum() {
        return connectNum;
    }

    /**
     * connectNumを設定する。
     * @param connectNum connectNum.
     */
    public void setConnectNum(String connectNum) {
        this.connectNum = connectNum;
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
     * subscriptionTypeSetを取得する。
     * @return subscriptionTypeSet subscriptionTypeSet。
     */
    public String getSubscriptionTypeSet() {
        return subscriptionTypeSet;
    }

    /**
     * subscriptionTypeSetを設定する。
     * @param subscriptionTypeSet subscriptionTypeSet.
     */
    public void setSubscriptionTypeSet(String subscriptionTypeSet) {
        this.subscriptionTypeSet = subscriptionTypeSet;
    }

    /**
     * dataFlowSetを取得する。
     * @return dataFlowSet dataFlowSet。
     */
    public String getDataFlowSet() {
        return dataFlowSet;
    }

    /**
     * dataFlowSetを設定する。
     * @param dataFlowSet dataFlowSet.
     */
    public void setDataFlowSet(String dataFlowSet) {
        this.dataFlowSet = dataFlowSet;
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
     * ソート順番を設定する。
     * @inheritDoc
     */
    @Override
    public int compareTo(LsPortBean o) {
        String key1 = rtcName + name + connectNum + portType + dataType + subscriptionTypeSet + dataFlowSet + interfaceType;
        String key2 = o.getRtcName() + o.getName() + o.getConnectNum() + o.getPortType() + o.getDataType()
                    + o.getSubscriptionTypeSet() + o.getDataFlowSet() + o.getInterfaceType();
        return key1.compareTo(key2);
    }

}
