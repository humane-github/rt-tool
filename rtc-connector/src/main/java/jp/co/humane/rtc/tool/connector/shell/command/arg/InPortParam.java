/**
 *
 */
package jp.co.humane.rtc.tool.connector.shell.command.arg;

/**
 * INポートのパラメータ情報。
 * @author terada
 *
 */
public class InPortParam {

    /** RTC名 */
    private String rtcName = null;

    /** ポート名 */
    private String portName = null;

    /**
     * rtcNameを取得する。
     * @return rtcName rtcName。
     */
    public String getRtcName() {
        return rtcName;
    }

    /**
     * rtcNameを設定する。
     * @param rtcName rtcName.
     */
    public void setRtcName(String rtcName) {
        this.rtcName = rtcName;
    }

    /**
     * portNameを取得する。
     * @return portName portName。
     */
    public String getPortName() {
        return portName;
    }

    /**
     * portNameを設定する。
     * @param portName portName.
     */
    public void setPortName(String portName) {
        this.portName = portName;
    }
}
