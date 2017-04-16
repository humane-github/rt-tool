/**
 *
 */
package jp.co.humane.rtc.tool.eventrecorder.sender;

import RTC.CameraImage;
import RTC.ReturnCode_t;
import RTC.TimedOctetSeq;
import jp.co.humane.opencvlib.OpenCVLib;
import jp.co.humane.rtc.common.component.DataFlowComponent;
import jp.co.humane.rtc.common.port.RtcOutPort;
import jp.co.humane.rtc.common.starter.RtcStarter;
import jp.co.humane.rtc.common.util.CorbaObj;
import jp.co.humane.rtc.tool.eventrecorder.sender.runnable.ConsoleListener;
import jp.go.aist.rtm.RTC.Manager;

/**
 * ファイルに記録されたイベント情報を出力ポートに送信する。
 * @author terada
 *
 */
public class EventSender extends DataFlowComponent<EventSenderConfig> {

    /** カメラ映像の送信ポート */
    protected RtcOutPort<CameraImage> cameraImageOut = new RtcOutPort<>("CameraImage", CorbaObj.newCameraImage());

    /** 音声データの送信ポート */
    protected RtcOutPort<TimedOctetSeq> voiceDataOut = new RtcOutPort<>("voiceData", CorbaObj.newTimedOctetSeq());

    /** コンソール入力処理 */
    protected ConsoleListener consoleListener = null;

    /** OpenCVの読み込み(デバッグ用) */
    static {
        OpenCVLib.LoadDLL();
    }

    /**
     * コンストラクタ。
     * @param manager RTCマネージャ。
     */
    public EventSender(Manager manager) {
        super(manager);
    }

    /**
     * アクティブ時の処理。
     * 入力ファイルをオープンする。
     *
     * @param ec_id ExecutionContext ID.
     * @return リターンコード。
     */
    @Override
    protected ReturnCode_t onRtcActivated(int ec_id) {

        // データファイルが存在しない場合はアクティブ化失敗とする
        if (!ConsoleListener.existsDataFile(config.getDataDir())) {
            logger.error(config.getDataDir() + "にデータファイルが存在しないためアクティブ化に失敗しました。");
            return ReturnCode_t.RTC_ERROR;
        }

        // コンソールからイベント送信処理を行う
        consoleListener = new ConsoleListener(config, cameraImageOut, voiceDataOut);
        new Thread(consoleListener).start();

        return ReturnCode_t.RTC_OK;
    }



    /**
     * 非アクティブ化時の処理。
     * イベント送信処理を中断する。
     *
     * @inheritDoc
     */
    @Override
    protected ReturnCode_t onRtcDeactivated(int ec_id) {
        consoleListener.stop();
        return ReturnCode_t.RTC_OK;
    }

    /**
     * メイン処理。
     * @param args 起動引数。
     */
    public static void main(String[] args) {

        // デバッグ用
        System.setProperty("java.library.path", "D:\\work\\dev\\opencv\\opencv\\build\\java\\x86\\opencv_java249.dll");

        RtcStarter.init(args)
                  .setConfig(new EventSenderConfig())
                  .start(EventSender.class);
    }

}
