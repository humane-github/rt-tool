/**
 *
 */
package jp.co.humane.rtc.tool.eventrecorder.sender.runnable;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.Semaphore;

import org.opencv.core.Mat;

import RTC.CameraImage;
import RTC.TimedOctetSeq;
import jp.co.humane.opencvlib.MatViewer;
import jp.co.humane.opencvlib.OpenCVLib;
import jp.co.humane.rtc.common.logger.RtcLogger;
import jp.co.humane.rtc.common.port.RtcOutPort;
import jp.co.humane.rtc.tool.eventrecorder.bean.RecordData;
import jp.co.humane.rtc.tool.eventrecorder.bean.RecordLastData;
import jp.co.humane.rtc.tool.eventrecorder.sender.EventSenderConfig;

/**
 * イベント送信を行う
 * @author terada.
 *
 */
public class SendEventRunner implements Runnable {

    /** OpenCV読み込み状態 */
    private static boolean isOpenCvLoaded = false;

    /** ロガー */
    private RtcLogger logger = new RtcLogger("SendEvent");

    /** データファイルのパス */
    private Path dataFilePath = null;

    /** 設定情報 */
    private EventSenderConfig config = null;

    /** カメラ映像の送信ポート */
    protected RtcOutPort<CameraImage> cameraImageOut = null;

    /** 音声データの送信ポート */
    protected RtcOutPort<TimedOctetSeq> voiceDataOut = null;

    /** イメージ確認用ビューア(デバッグ用) */
    private MatViewer imageViewer = new MatViewer("SendEvent");

    /** 送信中フラグ */
    private boolean isSending = true;

    /** セマフォ */
    private Semaphore endWatch = new Semaphore(1);

    /**
     * コンストラクタ。
     * @param dataFilePath データファイルのパス。
     */
    public SendEventRunner(Path dataFilePath,
                           EventSenderConfig config,
                           RtcOutPort<CameraImage> cameraImageOut,
                           RtcOutPort<TimedOctetSeq> voiceDataOut) {
        this.dataFilePath = dataFilePath;
        this.config = config;
        this.cameraImageOut = cameraImageOut;
        this.voiceDataOut = voiceDataOut;

        // ビューアを表示する場合はOpenCVを初期化
        if (!isOpenCvLoaded && config.getEnableViewer()) {
            OpenCVLib.LoadDLL();
            isOpenCvLoaded = true;
        }
    }

    /**
     * スレッド処理のエントリポイント。
     * @inheritDoc
     */
    @Override
    public void run() {

        ObjectInputStream stream = null;
        try {
            endWatch.acquire();
            while (isSending) {
                stream = readFile();
                sendEvent(stream);
                stream.close();
                stream = null;
            }
        } catch (ClassNotFoundException | IOException e) {
            String msg = "データファイルの読み込みに失敗しました。";
            logger.error(msg, e);
            throw new RuntimeException(msg, e);

        } catch (InterruptedException e) {
            // do nothing
        } finally {
            if (null != stream) {
                try {
                    stream.close();
                } catch (IOException e) {
                    // do nothing
                }
            }
            endWatch.release();
        }
    }

    /**
     * データファイルを読み込む。
     * @return ObjectInputStream.
     */
    private ObjectInputStream readFile() {

        try {

            return new ObjectInputStream(Files.newInputStream(dataFilePath));

        } catch (IOException ex) {
            logger.error(dataFilePath.toString() + "のオープンに失敗しました", ex);
            throw new RuntimeException(ex);

        }
    }

    /**
     * データファイルの情報を出力ポートに送信する。
     *
     * @throws IOException ファイル読み込み失敗時にスロー。
     * @throws ClassNotFoundException データ不正時にスロー。
     */
    private void sendEvent(ObjectInputStream stream) throws ClassNotFoundException, IOException, InterruptedException {

        while(true) {

            // 送信中断時はループ終了
            if (!isSending) {
                break;
            }

            // ファイルからオブジェクトを読み込む
            RecordData data = (RecordData)stream.readObject();
            if (!(data instanceof RecordData)) {
                String msg = dataFilePath.toString() + "に不正なデータが存在します：" + data.getClass().getCanonicalName();
                logger.error(msg);
                throw new RuntimeException(msg);
            }

            // 次の送信までの待ち時間と送信データを取得
            long waitTime = (long) (data.getInterval() * config.getSpeedRatio() * 0.01);
            Object obj = data.getData();

            // 最終データに達した場合はループ終了
            if (obj instanceof RecordLastData) {
                break;
            }

            // カメライメージの場合
            if (obj instanceof CameraImage) {

                Thread.sleep(waitTime);

                // ビューア表示ありの場合
                CameraImage image = (CameraImage) obj;
                if (config.getEnableViewer()) {
                    Mat cameraMat = new Mat(image.height, image.width, image.bpp);
                    cameraMat.put(0, 0, image.pixels);
                    imageViewer.updateImage(cameraMat);
                }
                cameraImageOut.write((CameraImage)obj);
                continue;
            }

            // 音声データの場合
            if (obj instanceof TimedOctetSeq) {

                Thread.sleep(waitTime);
                voiceDataOut.write((TimedOctetSeq) obj);
                continue;

            }
        }
    }

    /**
     * 処理を終了させる。
     * @param th この処理を実行しているスレッド。
     */
    public void stop(Thread th) {
        try {
            isSending = false;
            th.interrupt();     // Sleep中であればSleep終了を待たずに処理を終了させる
            endWatch.acquire();
        } catch (InterruptedException e) {
            // do nothing
        }
    }

}
