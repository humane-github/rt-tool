/**
 *
 */
package jp.co.humane.rtc.tool.connector.shell.command;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.CommandMarker;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.stereotype.Component;

import jp.co.humane.rtc.tool.connector.dao.NamingServerDao;
import jp.co.humane.rtc.tool.connector.shell.command.arg.ConfigParam;
import jp.co.humane.rtc.tool.connector.shell.command.arg.RtcParam;

/**
 * configコマンドの処理を定義。
 * @author terada.
 *
 */
@Component
public class ConfigCommand  implements CommandMarker {

    /** ネーミングサーバーDAO */
    @Autowired
    private NamingServerDao dao = null;

    @CliCommand(value = "config", help = "Update RTC Configuration")
    public String simple(
            @CliOption(key = { "rtc" }, mandatory = true, help = "RTC Component", specifiedDefaultValue = "") final RtcParam rtcParam,
            @CliOption(key = { "param" }, mandatory = true, help = "Configuration Parameter", specifiedDefaultValue = "") final ConfigParam configParam,
            @CliOption(key = { "value" }, mandatory = true, help = "Configuration Value", specifiedDefaultValue = "") final String value
            ) {

        String msg = "設定を更新しました。";
        try {
            dao.updateConfig(rtcParam.getName(), configParam.getName(), value);
        } catch (RuntimeException ex) {
            msg = ex.getMessage();
        }

        return msg;
    }
}
