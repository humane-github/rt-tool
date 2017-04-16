/**
 *
 */
package jp.co.humane.rtc.tool.eventrecorder.sender.runnable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import RTC.CameraImage;
import RTC.TimedOctetSeq;
import jp.co.humane.rtc.common.logger.RtcLogger;
import jp.co.humane.rtc.common.port.RtcOutPort;
import jp.co.humane.rtc.tool.eventrecorder.sender.EventSenderConfig;

/**
 * イベント送信のトリガーをコンソール入力から行う処理。
 * @author terada
 *
 */
public class ConsoleListener implements Runnable {

    /** データファイルのパターン */
    private static final String DATA_FILE_GLOB = "*.dat";

    /** ロガー */
    private static final RtcLogger logger = new RtcLogger("ConsoleListener");

    /** メッセージフォーマット */
    private static final String MESSAGE_FORMAT = "%3d  %s%n";

    /** 設定情報 */
    private EventSenderConfig config = null;

    /** カメラ映像の送信ポート */
    protected RtcOutPort<CameraImage> cameraImageOut = null;

    /** 音声データの送信ポート */
    protected RtcOutPort<TimedOctetSeq> voiceDataOut = null;

    /** イベント送信スレッド */
    protected Thread sendEventThread = null;

    /** イベント送信処理 */
    protected SendEventRunner sender = null;

    /**
     * コンストラクタ。
     * @param config         設定情報。
     * @param cameraImageOut カメラ映像の送信ポート。
     * @param voiceDataOut   音声データの送信ポート。
     */
    public ConsoleListener(EventSenderConfig config,
                           RtcOutPort<CameraImage> cameraImageOut,
                           RtcOutPort<TimedOctetSeq> voiceDataOut) {
        this.config = config;
        this.cameraImageOut = cameraImageOut;
        this.voiceDataOut = voiceDataOut;
    }

    /**
     * コンソールからイベント送信データと送信のタイミングを取得する。
     * @inheritDoc
     */
    public void run() {

        while (true) {

            // イベント送信する対象のデータファイルを選択させる
            Path dataFile = getSendEventDataFile();
            if (null == dataFile) {
                return;
            }

            // 送信処理を行う
            sender = new SendEventRunner(dataFile, config, cameraImageOut, voiceDataOut);
            sendEventThread = new Thread(sender);
            sendEventThread.start();

            // コンソールから送信終了のトリガーを受け取る
            println("エンターキー押下で送信を終了します。：");
            getConsoleInput();

            // 送信終了をコンソールに出力
            stop();
            sendEventThread = null;
            System.out.println("送信を終了しました。");
        }
    }

    /**
     * イベント送信対象のデータファイルを取得する
     * @return データファイルのパス。終了する場合はnull。
     */
    private Path getSendEventDataFile() {

        // データファイルをコンソールから選ばせる
        while (true) {

            // 存在するデータファイルの一覧を取得
            List<Path> fileList = getDataFileList(config.getDataDir());
            if (fileList.isEmpty()) {
                println("イベント送信対象となるデータファイルが存在しないため処理を終了します。");
                return null;
            }

            // データファイルを番号で選ばせる
            String msg = createDataFileSelectMessage(fileList);
            println(msg);
            String inData = getConsoleInput();

            // -1の場合は処理終了
            if (Objects.equals("-1", inData)) {
                println("処理を終了しました。");
                println("再度送信する場合はdeactive後に再実行してください。");
                return null;
            }

            // リストの範囲内であればそのファイルを送信対象として使用する
            int inNumData = parseInt(inData);
            if (0 <= inNumData && inNumData < fileList.size()) {
                return fileList.get(inNumData);
            }

            println("入力値が不正です。再入力してください。");
        }
    }

    /**
     * データファイル選択用のメッセージを作成する。
     * @param fileList データファイル一覧。
     * @return メッセージ。
     */
    private String createDataFileSelectMessage(List<Path> fileList) {

        StringBuilder sb = new StringBuilder(System.lineSeparator());
        sb.append(String.format(MESSAGE_FORMAT, -1, "処理を終了する"));
        for (int i = 0; i < fileList.size(); i++) {
            String fileName = fileList.get(i).getFileName().toString();
            sb.append(String.format(MESSAGE_FORMAT, i, fileName));
        }
        sb.append("イベント送信する対象データファイルを番号で入力してください：");

        return sb.toString();
    }

    /**
     * 指定ディレクトリ配下にデータファイルが存在するかチェックする。
     * @param path ディレクトリのパス。
     * @return 存在する場合はtrue。
     */
    public static boolean existsDataFile(String path) {
        List<Path> dataFiles = getDataFileList(path);
        return (0 != dataFiles.size());
    }

    /**
     * 指定ディレクトリ配下のデータファイルの一覧を取得する。
     * @param path ディレクトリのパス。
     * @return データファイルのリスト。
     */
    private static List<Path> getDataFileList(String path) {

        List<Path> fileList = new ArrayList<>();

        // 指定ディレクトリ配下のデータファイル一覧を取得
        Path dir = Paths.get(path);
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir, DATA_FILE_GLOB)) {
            for (Path p : stream) {
                fileList.add(p);
            }
        } catch (IOException e) {
            logger.error(path.toString() + "配下のデータファイル一覧取得に失敗しました。");
        }

        return fileList;
    }

    /**
     * 処理を中断する。
     */
    public void stop() {
        if (null != sendEventThread) {
            sender.stop(sendEventThread);
        }
    }

    /**
     * コンソール出力する。
     * @param msg 出力メッセージ。
     */
    private void println(String msg) {
        System.out.println(msg + System.lineSeparator());
    }

    /**
     * コンソールから入力された文字列を取得する。
     * @return 入力された文字列。
     */
    private String getConsoleInput() {

        try {
            InputStreamReader isr = new InputStreamReader(System.in);
            BufferedReader br = new BufferedReader(isr);
            return br.readLine();
        } catch (IOException ex) {
            String msg = "コンソールからの入力情報の取得に失敗しました。";
            logger.error(msg, ex);
            throw new RuntimeException(msg, ex);
        }
    }

    /**
     * 文字列を数値に変換する。
     * 変換できない場合は-1を返す。
     * @param s 変換対象文字列。
     * @return 数値。
     */
    private int parseInt(String s) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException ex) {
            return -1;
        }
    }

}
