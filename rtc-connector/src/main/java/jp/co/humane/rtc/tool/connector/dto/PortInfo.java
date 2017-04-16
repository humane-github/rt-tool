/**
 *
 */
package jp.co.humane.rtc.tool.connector.dto;

import java.util.Set;

/**
 * ポートの情報を格納するBean。
 * @author terada.
 *
 */
public class PortInfo {

    /** ポート名 */
    private String name = null;

    /** ポートがINポートか否か */
    private boolean isInPort = false;

    /** データタイプ */
    private String dataType = null;

    /** 利用可能なサブスクリプションタイプセット */
    private Set<String> subscriptionTypeSet = null;

    /** 利用可能なデータフロータイプセット */
    private Set<String> dataFlowSet = null;

    /** インターフェースタイプ */
    private String interfaceType = null;


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
     * isInPortを取得する。
     * @return isInPort isInPort。
     */
    public boolean isInPort() {
        return isInPort;
    }

    /**
     * isInPortを設定する。
     * @param isInPort isInPort.
     */
    public void setIsInPort(boolean isInPort) {
        this.isInPort = isInPort;
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
    public Set<String> getSubscriptionTypeSet() {
        return subscriptionTypeSet;
    }

    /**
     * subscriptionTypeSetを設定する。
     * @param subscriptionTypeSet subscriptionTypeSet.
     */
    public void setSubscriptionTypeSet(Set<String> subscriptionTypeSet) {
        this.subscriptionTypeSet = subscriptionTypeSet;
    }

    /**
     * dataFlowSetを取得する。
     * @return dataFlowSet dataFlowSet。
     */
    public Set<String> getDataFlowSet() {
        return dataFlowSet;
    }

    /**
     * dataFlowSetを設定する。
     * @param dataFlowSet dataFlowSet.
     */
    public void setDataFlowSet(Set<String> dataFlowSet) {
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
}
