/**
 *
 */
package jp.co.humane.rtc.tool.connector.shell.cfg;

import org.springframework.shell.plugin.support.DefaultPromptProvider;
import org.springframework.stereotype.Component;

/**
 * RtcConnectorのプロンプトを設定。
 * @author terada.
 *
 */
@Component
public class PromptConfig extends DefaultPromptProvider {

    /**
     * プロンプトを設定。
     * @inheritDoc
     */
    @Override
    public String getPrompt() {
        return "rtc-connector>";
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
