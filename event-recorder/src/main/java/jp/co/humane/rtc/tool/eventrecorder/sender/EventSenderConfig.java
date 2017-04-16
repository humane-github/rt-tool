/**
 *
 */
package jp.co.humane.rtc.tool.eventrecorder.sender;

import java.io.File;

import jp.co.humane.rtc.common.starter.bean.ConfigBase;

/**
 * EventSenderの設定情報。
 * @author terada
 *
 */
public class EventSenderConfig extends ConfigBase {

    /** データファイル配置ディレクトリ */
    private String dataDir = System.getProperty("user.dir") + File.separator + "data";

    /** 速さの割合(%) */
    private Integer speedRatio = 100;

    /** ビューア */
    private Boolean enableViewer = false;

    /**
     * デフォルトコンストラクタ。
     */
    public EventSenderConfig() {
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
     * speedRatioを取得する。
     * @return speedRatio speedRatio。
     */
    public Integer getSpeedRatio() {
        return speedRatio;
    }

    /**
     * speedRatioを設定する。
     * @param speedRatio speedRatio.
     */
    public void setSpeedRatio(Integer speedRatio) {
        this.speedRatio = speedRatio;
    }

    /**
     * enableViewerを取得する。
     * @return enableViewer enableViewer。
     */
    public Boolean getEnableViewer() {
        return enableViewer;
    }

    /**
     * enableViewerを設定する。
     * @param enableViewer enableViewer.
     */
    public void setEnableViewer(Boolean enableViewer) {
        this.enableViewer = enableViewer;
    }
 }
