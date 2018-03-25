package jp.co.humane.rtc.tool.connector.component;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

/**
 * 接続設定を保持するクラス。
 * @author teradakng
 *
 */
@Component
public class ConnectProfileHolder {

    /** デフォルトの接続設定 */
    private static final Map<String, String> defaultProfile = new HashMap<>();
    static {
        defaultProfile.put("dataport.interface_type",    "corba_cdr");
        defaultProfile.put("dataport.dataflow_type",     "push");
        defaultProfile.put("dataport.subscription_type", "new");
    }

    /** 接続設定 */
    private static final Map<String, String> connectProfile = new HashMap<>();

    /**
     * デフォルトコンストラクタ。
     */
    public ConnectProfileHolder() {
        restoreDefault();
    }

    /**
     * プロファイル情報を取得する。
     * @return プロファイル情報。
     */
    public Map<String, String> getProfiles() {
        return connectProfile;
    }

    /**
     * デフォルト値を復元する。
     */
    public void restoreDefault() {
        connectProfile.clear();
        defaultProfile.forEach((key, value) -> {
            connectProfile.put(key, value);
        });
    }
}
