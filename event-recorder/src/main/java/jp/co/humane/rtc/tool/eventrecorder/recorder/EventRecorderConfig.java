/**
 *
 */
package jp.co.humane.rtc.tool.eventrecorder.recorder;

import java.io.File;

import jp.co.humane.rtc.common.starter.bean.ConfigBase;

/**
 * EventRecorderの設定情報。
 * @author terada
 *
 */
public class EventRecorderConfig extends ConfigBase {

    /** データファイル配置ディレクトリ */
    private String dataDir = System.getProperty("user.dir") + File.separator + "data";

    /** ファイル名 */
    private String fileName = "eventdata.dat";

    /**
     * デフォルトコンストラクタ。
     */
    public EventRecorderConfig() {
        super();
    }

    /**
     * dataDirを取得する。
     * @return dataDir dataDir。
     */
    public String getDataDir() {
        return dataDir;
    }

    /**
     * dataDirを設定する。
     * @param dataDir dataDir.
     */
    public void setDataDir(String dataDir) {
        this.dataDir = dataDir;
    }

    /**
     * fileNameを取得する。
     * @return fileName fileName。
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * fileNameを設定する。
     * @param fileName fileName.
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
