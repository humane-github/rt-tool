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
import jp.co.humane.rtc.tool.connector.shell.command.arg.InPortParam;
import jp.co.humane.rtc.tool.connector.shell.command.arg.OutPortParam;

/**
 * connectコマンドの処理を定義。
 * @author terada.
 *
 */
@Component
public class ConnectCommand implements CommandMarker {

    /** ネーミングサーバーDAO */
    @Autowired
    private NamingServerDao dao = null;

    @CliCommand(value = "connect", help = "Connect RTC Ports")
    public String simple(
            @CliOption(key = { "inPort" }, mandatory = true, help = "In Port To Connect", specifiedDefaultValue = "") final InPortParam inPort,
            @CliOption(key = { "outPort" }, mandatory = true, help = "Out Port To Connect", specifiedDefaultValue = "") final OutPortParam outPort,
            @CliOption(key = { "dataflow" }, mandatory = false, help = "Connection Dataflow Type", specifiedDefaultValue = "") final String dataflow,
            @CliOption(key = { "subscription" }, mandatory = false, help = "Connection Subscription Type", specifiedDefaultValue = "") final String subscription
            ) {

        String msg = inPort.getPortName() + "/" + outPort.getPortName();

        return msg;
    }


}
