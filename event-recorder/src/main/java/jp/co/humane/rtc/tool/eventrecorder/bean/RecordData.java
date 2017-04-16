/**
 *
 */
package jp.co.humane.rtc.tool.eventrecorder.bean;

import java.io.Serializable;

/**
 * 記録用データ格納クラス。
 * @author terada.
 *
 */
public class RecordData implements Serializable {

    /** 前のデータとのインターバル */
    private long interval = 0;

    /** データ */
    private Serializable data = null;

    /**
     * コンストラクタ。
     * @param interval 前のイベントデータとのインターバル。
     * @param data     イベントデータ。
     */
    public RecordData(long interval, Serializable data) {
        this.interval = interval;
        this.data = data;
    }

    /**
     * intervalを取得する。
     * @return interval interval。
     */
    public long getInterval() {
        return interval;
    }

    /**
     * intervalを設定する。
     * @param interval interval.
     */
    public void setInterval(long interval) {
        this.interval = interval;
    }

    /**
     * dataを取得する。
     * @return data data。
     */
    public Serializable getData() {
        return data;
    }

    /**
     * dataを設定する。
     * @param data data.
     */
    public void setData(Serializable data) {
        this.data = data;
    }
}
