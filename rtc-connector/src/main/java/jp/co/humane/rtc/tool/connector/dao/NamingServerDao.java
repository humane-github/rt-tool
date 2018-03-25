/**
 *
 */
package jp.co.humane.rtc.tool.connector.dao;

import java.util.List;
import java.util.Map;

import jp.co.humane.rtc.tool.connector.dto.ConnectInfo;
import jp.co.humane.rtc.tool.connector.dto.PortInfo;
import jp.co.humane.rtc.tool.connector.dto.RtcInfo;

/**
 * ネーミングサービスのデータにアクセスするDAO。
 * @author terada.
 *
 */
public interface NamingServerDao {

    /**
     * RTC情報の一覧を取得する。
     * @return RTC情報の一覧。
     */
    public List<RtcInfo> getRtcList();

    /**
     * RTC情報を取得する。
     * @param rtcName RTC名。
     * @return RTC情報。
     */
    public RtcInfo getRtcInfo(String rtcName);

    /**
     * ポート情報のマップを取得する。
     * @return ポート情報のマップ。
     */
    public Map<String, List<PortInfo>> getPortMap();

    /**
     * ポートの接続数を取得する。
     * @param rtcName   RTC名。
     * @param portName  ポート名。
     * @return 接続数。
     */
    public int getConnectNum(String rtcName, String portName);

    /**
     * 接続情報のリストを取得する。
     * @return 接続情報のリスト。
     */
    public List<ConnectInfo> getConnectInfoList();

    /**
     * 設定情報のマップ（key:RTC名、value:設定マップ）を取得する。
     * @return 設定情報のマップ。
     */
    public Map<String, Map<String, String>> getConfigMap();

    /**
     * 設定情報を更新する。
     * @param rtcName    RTC名。
     * @param paramName  パラメータ名。
     * @param value      値。
     */
    public void updateConfig(String rtcName, String paramName, String value);

    /**
     * 2つのポートを接続する。
     * @param outRtcName  出力ポートを持つRTC名。
     * @param outPortName 出力ポート名。
     * @param inRtcName   入力ポートを持つRTC名。
     * @param inPortName  入力ポート名。
     */
    public void connect(String outRtcName, String outPortName, String inRtcName, String inPortName);

    /**
     * 2つのポートを切断する。
     * @param outRtcName  出力ポートを持つRTC名。
     * @param outPortName 出力ポート名。
     * @param inRtcName   入力ポートを持つRTC名。
     * @param inPortName  入力ポート名。
     */
    public void disconnect(String outRtcName, String outPortName, String inRtcName, String inPortName);

    /**
     * RTCをアクティブ化する。
     * @param rtcName RTC名。
     */
    public void activate(String rtcName);

    /**
     * オブジェクト参照のマップを更新する。
     */
    public void reflesh();


}
