package jp.co.humane.rtc.tool.connector.shell.command;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.CommandMarker;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.stereotype.Component;

import jp.co.humane.rtc.tool.connector.common.exceptions.ApplicationException;
import jp.co.humane.rtc.tool.connector.dao.NamingServerDao;
import jp.co.humane.rtc.tool.connector.shell.command.arg.RtcParam;

/**
 * activateコマンドの処理を定義。
 * @author teradakng
 *
 */
@Component
public class ActivateCommand implements CommandMarker {

    /** ネーミングサーバーDAO */
    @Autowired
    private NamingServerDao dao = null;

    @CliCommand(value = "activate", help = "Activate RTC")
    public String simple(
            @CliOption(key = { "rtc" }, mandatory = true, help = "RTC Component", specifiedDefaultValue = "") final RtcParam rtcParam
            ) {

        // 最新の情報を使用する
        dao.reflesh();

        // アクティブ化を実施
        String msg = "アクティブ化を行いました。";
        try {
            dao.activate(rtcParam.getName());
        } catch (ApplicationException ex) {
            msg = ex.getMessage();
        }

        return msg;
    }
}
