/**
 *
 */
package jp.co.humane.rtc.tool.connector.shell.command.table;

import java.util.LinkedHashMap;

/**
 * connect-profile --showコマンド実行結果を格納するBean.
 * @author terada.
 *
 */
public class ConnectProfileShowBean implements Comparable<ConnectProfileShowBean> {

    /** ヘッダ情報 */
    public static final LinkedHashMap<String, Object> HEADER;
    static{
        HEADER = new LinkedHashMap<>();
        HEADER.put("key",    "Key");
        HEADER.put("value",  "Value");
    }

    /** キー値 */
    private String key = "";

    /** 値 */
    private String value = "";

    /**
     * keyを取得する。
     * @return key keyの値。
     */
    public String getKey() {
        return key;
    }

    /**
     * keyを設定する。
     * @param key keyの値。
     */
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * valueを取得する。
     * @return value valueの値。
     */
    public String getValue() {
        return value;
    }

    /**
     * valueを設定する。
     * @param value valueの値。
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * ソート順番を設定。
     * @inheritDoc
     */
    @Override
    public int compareTo(ConnectProfileShowBean o) {
        return key.compareTo(o.getKey());
    }
}
