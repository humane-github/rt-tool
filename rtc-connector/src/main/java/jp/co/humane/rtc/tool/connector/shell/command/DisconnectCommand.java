package jp.co.humane.rtc.tool.connector.shell.command;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.CommandMarker;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.stereotype.Component;

import jp.co.humane.rtc.tool.connector.common.exceptions.ApplicationException;
import jp.co.humane.rtc.tool.connector.dao.NamingServerDao;
import jp.co.humane.rtc.tool.connector.shell.command.arg.InPortParam;
import jp.co.humane.rtc.tool.connector.shell.command.arg.OutPortParam;

/**
 * disconnectコマンドの処理を定義。
 * @author teradakng
 *
 */
@Component
public class DisconnectCommand implements CommandMarker {

    /** ネーミングサーバーDAO */
    @Autowired
    private NamingServerDao dao = null;

    @CliCommand(value = "disconnect", help = "Disconnect RTC")
    public String simple(
            @CliOption(key = { "inPort" }, mandatory = true, help = "In Port To Connect", specifiedDefaultValue = "") final InPortParam inPort,
            @CliOption(key = { "outPort" }, mandatory = true, help = "Out Port To Connect", specifiedDefaultValue = "") final OutPortParam outPort
            ) {

        // 最新の情報を使用する
        dao.reflesh();

        // 切断を行う
        String msg = "切断しました。";
        try {
            dao.disconnect(outPort.getRtcName(), outPort.getPortName(), inPort.getRtcName(), inPort.getPortName());
        } catch (ApplicationException ex) {
            msg = ex.getMessage();
        }
        return msg;
    }
}
