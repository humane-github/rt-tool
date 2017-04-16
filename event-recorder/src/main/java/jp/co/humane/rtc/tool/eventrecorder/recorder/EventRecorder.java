package jp.co.humane.rtc.tool.eventrecorder.recorder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;

import RTC.CameraImage;
import RTC.ReturnCode_t;
import RTC.TimedOctetSeq;
import jp.co.humane.rtc.common.component.DataFlowComponent;
import jp.co.humane.rtc.common.port.RtcInPort;
import jp.co.humane.rtc.common.starter.RtcStarter;
import jp.co.humane.rtc.common.util.CorbaObj;
import jp.co.humane.rtc.common.util.SleepTimer;
import jp.co.humane.rtc.tool.eventrecorder.bean.RecordData;
import jp.co.humane.rtc.tool.eventrecorder.bean.RecordLastData;
import jp.go.aist.rtm.RTC.Manager;

/**
 * 入力ポートで受信したデータをファイルに記録する。
 * 「http://soundoftext.com/」で音声データを取得し、NETDUETTO β2経由で録音できる。
 * Virtual Webcam経由で映像を録画できる。
 * 素材は「http://www.clipconverter.cc/download/OLnDSUxA/330316442/」
 * @author terada
 *
 */
public class EventRecorder extends DataFlowComponent<EventRecorderConfig> {

    /** カメラ映像の受信ポート */
    protected RtcInPort<CameraImage> cameraImageIn = new RtcInPort<>("CameraImage", CorbaObj.newCameraImage());

    /** 先生の受信ポート */
    protected RtcInPort<TimedOctetSeq> voiceDataIn = new RtcInPort<>("VoiceData", CorbaObj.newTimedOctetSeq());

    /** イベント情報書き込み用ストリーム */
    protected ObjectOutputStream stream = null;

    /** 記録中フラグ */
    protected boolean isRecording = false;

    /** 前の受信時刻（ミリ秒） */
    protected long preAcceptTime = 0;

    /**
     * コンストラクタ。
     * @param manager RTCマネージャ。
     */
    public EventRecorder(Manager manager) {
        super(manager);
    }

    /**
     * アクティブ時の処理。
     * 出力ファイルをオープンする。
     *
     * @param ec_id ExecutionContext ID.
     * @return リターンコード。
     */
    @Override
    protected ReturnCode_t onRtcActivated(int ec_id) {

        // データ出力ディレクトリが存在しない場合はエラーとする
        Path dir = Paths.get(config.getDataDir());
        if (!Files.isDirectory(dir, LinkOption.NOFOLLOW_LINKS)) {
            logger.error("データ出力先ディレクトリ" + dir.toString() + "が存在しません。");
            return ReturnCode_t.RTC_ERROR;
        }


        try {
            // データ書き込み先ファイルをオープンする
            Path path = Paths.get(config.getDataDir(), config.getFileName());
            stream = new ObjectOutputStream(Files.newOutputStream(path));
        } catch (IOException ex) {
            logger.error(config.getFileName() + "のオープンに失敗しました", ex);
            return ReturnCode_t.RTC_ERROR;
        }

        // コンソールから記録開始・終了のトリガーを取得
        Runnable consoleWatch = new Runnable() {
            @Override
            public void run() {
                try {

                    InputStreamReader isr = new InputStreamReader(System.in);
                    BufferedReader br = new BufferedReader(isr);

                    // コンソールから記録開始のトリガーを受け取る
                    System.out.print("エンターキー押下で記録を開始します。：");
                    br.readLine();

                    // 現在時間を取得して記録中フラグをONにする
                    preAcceptTime = new Date().getTime();
                    isRecording = true;
                    System.out.println("記録中...");

                    // コンソールから記録終了のトリガーを受け取る
                    System.out.print("エンターキー押下で記録を終了します。：");
                    br.readLine();

                    // ファイル書き込みを終了する
                    isRecording = false;
                    SleepTimer.Sleep(100); // 書き込み途中のデータの書き込みを待つ
                    onRtcDeactivated(0);
                    System.out.println("記録を終了しました。");

                } catch (IOException ex) {
                    logger.error("コンソール入力の取得に失敗しました。", ex);
                }
            }
        };
        new Thread(consoleWatch).start();

        return ReturnCode_t.RTC_OK;
    }


    /**
     * 非アクティブ化時の処理。
     * ファイルをクローズする。
     */
    @Override
    protected ReturnCode_t onRtcDeactivated(int ec_id) {

        // ファイルをクローズ
        if (null != stream) {
            try {
                // 最終データをファイルに書き込む
                writeObject(new RecordData(0L, new RecordLastData()));
                stream.flush();
                stream.close();
                stream = null;
            } catch (IOException e) {
                logger.error(config.getFileName() + "のクローズ処理に失敗しました。", e);
                return ReturnCode_t.RTC_ERROR;
            }
        }
        return ReturnCode_t.RTC_OK;
    }

    /**
     * 周期的な処理。
     */
    @Override
    protected ReturnCode_t onRtcExecute(int ec_id) {

        // 記録中ではない場合は受信データを廃棄
        if (!isRecording) {
            cameraImageIn.clear();
            voiceDataIn.clear();
            return ReturnCode_t.RTC_OK;
        }

        while(true) {

            // 記録が終了したらループを抜ける
            if (!isRecording) {
                break;
            }

            // ポートにデータが入っていない場合は1ミリ秒待つ
            boolean isCamera = cameraImageIn.isNew();
            boolean isVoice = voiceDataIn.isNew();
            if (!isCamera && !isVoice) {
                SleepTimer.Sleep(1);
                continue;
            }

            // カメライメージをファイルに出力する
            if (isCamera) {
                CameraImage image = cameraImageIn.readData();
                RecordData data = getRecordData(image);
                writeObject(data);
            }

            // 音声データをファイルに出力する
            if (isVoice) {
                TimedOctetSeq voiceData = voiceDataIn.readData();
                RecordData data = getRecordData(voiceData);
                writeObject(data);
            }

        }

        return ReturnCode_t.RTC_OK;
    }

    /**
     * データをファイル出力用インスタンスに格納して返す。
     * @param data カメライメージデータ。
     * @return ファイル出力用インスタンス。
     */
    protected RecordData getRecordData(Serializable data) {
        long currentTime = new Date().getTime();
        RecordData recordData = new RecordData(currentTime - preAcceptTime, data);
        preAcceptTime = currentTime;
        return recordData;
    }

    /**
     * 指定データをファイルに出力する。
     * @param obj 出力データ。
     */
    protected void writeObject(Object obj) {
        try {
            stream.writeObject(obj);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * メイン処理。
     * @param args 起動引数。
     */
    public static void main(String[] args) {

        RtcStarter.init(args)
                  .setConfig(new EventRecorderConfig())
                  .start(EventRecorder.class);
    }

}
