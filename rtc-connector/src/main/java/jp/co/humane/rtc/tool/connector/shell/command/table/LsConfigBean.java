/**
 *
 */
package jp.co.humane.rtc.tool.connector.shell.command.table;

import java.util.LinkedHashMap;

/**
 * ls --configコマンド実行結果を格納するBean.
 * @author terada.
 *
 */
public class LsConfigBean implements Comparable<LsConfigBean> {

    /** ヘッダ情報 */
    public static final LinkedHashMap<String, Object> HEADER;
    static{
        HEADER = new LinkedHashMap<>();
        HEADER.put("rtcName",    "RTC-Name");
        HEADER.put("paramName",  "Param");
        HEADER.put("paramValue", "Value");
    }

    /** RTC名 */
    private String rtcName = "";

    /** パラメータ名 */
    private String paramName = "";

    /** パラメータ値 */
    private String paramValue = "";

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
     * paramNameを取得する。
     * @return paramName paramName。
     */
    public String getParamName() {
        return paramName;
    }

    /**
     * paramNameを設定する。
     * @param paramName paramName.
     */
    public void setParamName(String paramName) {
        this.paramName = paramName;
    }

    /**
     * paramValueを取得する。
     * @return paramValue paramValue。
     */
    public String getParamValue() {
        return paramValue;
    }

    /**
     * paramValueを設定する。
     * @param paramValue paramValue.
     */
    public void setParamValue(String paramValue) {
        this.paramValue = paramValue;
    }

    /**
     * ソート順番を設定。
     * @inheritDoc
     */
    @Override
    public int compareTo(LsConfigBean o) {
        String key1 = rtcName + paramName + paramValue;
        String key2 = o.getRtcName() + o.getParamName() + o.getParamValue();
        return key1.compareTo(key2);
    }




}
