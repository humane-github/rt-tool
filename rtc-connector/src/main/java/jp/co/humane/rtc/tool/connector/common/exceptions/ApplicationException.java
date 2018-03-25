package jp.co.humane.rtc.tool.connector.common.exceptions;

/**
 * 内部で発生した例外を扱う例外クラス。
 * @author teradakng
 *
 */
public class ApplicationException extends RuntimeException {

    /**
     * コンストラクタ。
     */
    public ApplicationException() {
        super();
    }

    /**
     * コンストラクタ。
     * @param message メッセージ。
     */
    public ApplicationException(String message) {
        super(message);
    }

    /**
     * コンストラクタ。
     * @param message メッセージ。
     * @param cause   エラー原因。
     */
    public ApplicationException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * コンストラクタ。
     * @param cause エラー原因。
     */
    public ApplicationException(Throwable cause) {
        super(cause);
    }
}
