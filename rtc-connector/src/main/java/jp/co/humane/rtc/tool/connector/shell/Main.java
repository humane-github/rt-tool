/**
 *
 */
package jp.co.humane.rtc.tool.connector.shell;

import java.io.IOException;

import org.springframework.shell.Bootstrap;

import jp.co.humane.rtc.tool.connector.RtcConnector;

/**
 * シェルを開始する。
 * @author terada.
 *
 */
public class Main {

    public static final String USAGE =
            "usage:\n"
          + "   RtcConnector server port [--cmdfile file]\n"
          + "     server : server name or ip address.\n"
          + "     port   : port number.\n"
          + "     file   : file written command list which is executed at start-up.\n\n";

    public static void main(String[] args) throws IOException {

        // TODO:後で消す
        args = new String[] { "localhost", "2809" };

        if (!checkArg(args)) {
            System.out.println(USAGE);
            return;
        }

        // Spring Shell起動時の処理をカスタマイズできないのでシステムプロパティ経由で情報を渡す
        System.setProperty(RtcConnector.SERVER_NAME, args[0]);
        System.setProperty(RtcConnector.PORT_NUMBER, args[1]);

        // Spring Shellの引数は2個減らす
        String[] newArgs = new String[args.length - 2];
        for (int i = 2; i < args.length; i++) {
            newArgs[i - 2] = args[i];
        }

        // Spring Shell起動
        Bootstrap.main(newArgs);
    }

    /**
     * 引数のチェックを行う。
     * @param args 引数情報。
     * @return チェック結果。
     */
    private static boolean checkArg(String[] args) {

        if (args.length < 2) {
            return false;
        }

        String port = args[1];
        try {
            Integer.parseInt(port);
        } catch (NumberFormatException ex) {
            return false;
        }

        return true;

    }

}
