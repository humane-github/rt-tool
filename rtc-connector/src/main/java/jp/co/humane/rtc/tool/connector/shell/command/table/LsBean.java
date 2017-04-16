/**
 *
 */
package jp.co.humane.rtc.tool.connector.shell.command.table;

import java.util.LinkedHashMap;

/**
 * lsコマンド実行結果を格納するBean.
 * @author terada.
 *
 */
public class LsBean implements Comparable<LsBean> {

    /** ヘッダ情報 */
    public static final LinkedHashMap<String, Object> HEADER;
    static{
        HEADER = new LinkedHashMap<>();
        HEADER.put("directory", "Directory");
        HEADER.put("id",        "RTC-Name");
        HEADER.put("kind",      "Kind");
        HEADER.put("state",     "State");
    }

    /** ディレクトリパス */
    private String directory = null;

    /** ID */
    private String id = null;

    /** KIND */
    private String kind = null;

    /** 状態 */
    private String state = null;

    /**
     * directoryを取得する。
     * @return directory directory。
     */
    public String getDirectory() {
        return directory;
    }

    /**
     * directoryを設定する。
     * @param directory directory.
     */
    public void setDirectory(String directory) {
        this.directory = directory;
    }

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
     * kindを取得する。
     * @return kind kind。
     */
    public String getKind() {
        return kind;
    }

    /**
     * kindを設定する。
     * @param kind kind.
     */
    public void setKind(String kind) {
        this.kind = kind;
    }

    /**
     * stateを取得する。
     * @return state state。
     */
    public String getState() {
        return state;
    }

    /**
     * stateを設定する。
     * @param state state.
     */
    public void setState(String state) {
        this.state = state;
    }

    /**
     * ソート順番を設定。
     * @inheritDoc
     */
    @Override
    public int compareTo(LsBean o) {
        String key1 = directory + id + kind + state;
        String key2 = o.getDirectory() + o.getId() + o.getKind() + o.getState();
        return key1.compareTo(key2);
    }
}
