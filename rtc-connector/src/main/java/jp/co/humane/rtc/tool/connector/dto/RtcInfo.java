/**
 *
 */
package jp.co.humane.rtc.tool.connector.dto;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import RTC.RTObject;
import jp.co.humane.rtc.tool.connector.common.enums.ComponentState;

/**
 * RTCのオブジェクト参照の情報を格納するBean.
 * @author terada.
 *
 */
public class RtcInfo {

    /** RTCのオブジェクト参照 */
    private RTObject objRef = null;

    /** ディレクトリパス */
    private String directory = null;

    /** ID */
    private String id = null;

    /** KIND */
    private String kind = null;

    /** 状態 */
    private ComponentState state = null;

    /** ポート名一覧 */
    private List<String> portNameList = new ArrayList<>();

    /** ポート情報マップ */
    private Map<String, PortInfo> portMap = new HashMap<>();

    /** 接続情報マップ(key：ポート名、value：接続情報一覧) */
    private Map<String, List<ConnectInfo>> connectMap = new HashMap<>();

    /** コンフィグ情報マップ(key:設定名、value:設定値) */
    private Map<String, String> configMap = new LinkedHashMap<>();

    /**
     * コンストラクタ。
     * @param object オブジェクト参照。
     */
    public RtcInfo(String directory, RTObject object, String id, String kind) {
        this.directory = directory;
        this.objRef = object;
        this.id = id;
        this.kind = kind;
    }

    /**
     * 接続情報を追加する。
     * @param portName    ポート名。
     * @param connectInfo 接続情報。
     */
    public void addConnectInfo(String portName, ConnectInfo connectInfo) {

        // ポート名が登録されている場合は接続情報を追加
        if (connectMap.containsKey(portName)) {
            connectMap.get(portName).add(connectInfo);
            return;
        }

        // ポート名が登録されていない場合はリストを追加
        connectMap.put(portName, Arrays.asList(connectInfo));
    }


    /**
     * objRefを取得する。
     * @return object object。
     */
    public RTObject getObjRef() {
        return objRef;
    }

    /**
     * objRefを設定する。
     * @param objRef objRef.
     */
    public void setObjRef(RTObject object) {
        this.objRef = object;
    }

    /**
     * idを取得する。
     * @return id id。
     */
    public String getId() {
        return id;
    }

    /**
     * idを設定する。
     * @param id id.
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * directoryを取得する。
     * @return directory directory。
     */
    public String getDirectory() {
        return directory;
    }

    /**
     * directoryを設定する。
     * @param directory directory.
     */
    public void setDirectory(String directory) {
        this.directory = directory;
    }

    /**
     * stateを取得する。
     * @return state state。
     */
    public ComponentState getState() {
        return state;
    }

    /**
     * stateを設定する。
     * @param state state.
     */
    public void setState(ComponentState state) {
        this.state = state;
    }

    /**
     * kindを取得する。
     * @return kind kind。
     */
    public String getKind() {
        return kind;
    }

    /**
     * kindを設定する。
     * @param kind kind.
     */
    public void setKind(String kind) {
        this.kind = kind;
    }

    /**
     * portNameListを取得する。
     * @return portNameList portNameList。
     */
    public List<String> getPortNameList() {
        return portNameList;
    }

    /**
     * portNameListを設定する。
     * @param portNameList portNameList.
     */
    public void setPortNameList(List<String> portNameList) {
        this.portNameList = portNameList;
    }


    /**
     * portMapを取得する。
     * @return portMap portMap。
     */
    public Map<String, PortInfo> getPortMap() {
        return portMap;
    }

    /**
     * portMapを設定する。
     * @param portMap portMap.
     */
    public void setPortMap(Map<String, PortInfo> portMap) {
        this.portMap = portMap;
    }

    /**
     * connectMapを取得する。
     * @return connectMap connectMap。
     */
    public Map<String, List<ConnectInfo>> getConnectMap() {
        return connectMap;
    }

    /**
     * connectMapを設定する。
     * @param connectMap connectMap.
     */
    public void setConnectMap(Map<String, List<ConnectInfo>> connectMap) {
        this.connectMap = connectMap;
    }

    /**
     * configMapを取得する。
     * @return configMap configMap。
     */
    public Map<String, String> getConfigMap() {
        return configMap;
    }

    /**
     * configMapを設定する。
     * @param configMap configMap.
     */
    public void setConfigMap(Map<String, String> configMap) {
        this.configMap = configMap;
    }

}
