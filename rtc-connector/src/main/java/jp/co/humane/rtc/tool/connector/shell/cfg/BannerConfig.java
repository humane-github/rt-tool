/**
 *
 */
package jp.co.humane.rtc.tool.connector.shell.cfg;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.shell.plugin.support.DefaultBannerProvider;
import org.springframework.shell.support.util.OsUtils;
import org.springframework.stereotype.Component;

/**
 * RtcConnectorのバナーを設定。
 * @author terada.
 *
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class BannerConfig extends DefaultBannerProvider {

    /**
     * バナーを設定。
     * @inheritDoc
     */
    @Override
    public String getBanner() {
        StringBuffer buf = new StringBuffer();
        buf.append("                        " + OsUtils.LINE_SEPARATOR);
        buf.append("========================" + OsUtils.LINE_SEPARATOR);
        buf.append("                        " + OsUtils.LINE_SEPARATOR);
        buf.append("      RTC Connector     " + OsUtils.LINE_SEPARATOR);
        buf.append("      Version:" + this.getVersion() + OsUtils.LINE_SEPARATOR);
        buf.append("                        " + OsUtils.LINE_SEPARATOR);
        buf.append("========================" + OsUtils.LINE_SEPARATOR);
        buf.append("                        " + OsUtils.LINE_SEPARATOR);
        return buf.toString();
    }

    /**
     * バージョンを指定。
     * @inheritDoc
     */
    @Override
    public String getVersion() {
        return "1.0.0";
    }

    /**
     * 初期メッセージを設定。
     * @inheritDoc
     */
    @Override
    public String getWelcomeMessage() {
        return "Welcome to RTC Connector CLI";
    }

    /**
     * 提供を設定。
     * @inheritDoc
     */
    @Override
    public String getProviderName() {
        return "Humane Systems co.";
    }

}
