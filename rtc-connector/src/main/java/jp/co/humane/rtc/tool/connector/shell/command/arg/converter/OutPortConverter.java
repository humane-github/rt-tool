/**
 *
 */
package jp.co.humane.rtc.tool.connector.shell.command.arg.converter;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.Completion;
import org.springframework.shell.core.Converter;
import org.springframework.shell.core.MethodTarget;
import org.springframework.stereotype.Component;

import jp.co.humane.rtc.tool.connector.dao.NamingServerDao;
import jp.co.humane.rtc.tool.connector.dto.PortInfo;
import jp.co.humane.rtc.tool.connector.shell.command.arg.OutPortParam;
import jp.co.humane.rtc.tool.connector.shell.common.ShellConsts;

/**
 * OUTポートパラメータのコンバータ。
 * @author terada.
 *
 */
@Component
public class OutPortConverter implements Converter<OutPortParam> {

    /** ネーミングサーバDao */
    @Autowired
    private NamingServerDao dao = null;

    /**
     * サポートする型を設定する。
     * @inheritDoc
     */
    @Override
    public boolean supports(Class<?> type, String optionContext) {
        return OutPortParam.class.isAssignableFrom(type);    }

    /**
     * 文字列からポートパラメータを生成する。
     * @inheritDoc
     */
    @Override
    public OutPortParam convertFromText(String value, Class<?> targetType, String optionContext) {

        OutPortParam portParam = new OutPortParam();

        // コロンが入っている場合はRTC名、ポート名で分けて確認
        int separatorPos = value.indexOf(ShellConsts.PORT_SEPARATOR);
        if (-1 != separatorPos) {

            String rtcName = value.substring(0, separatorPos);
            String portName = value.substring(separatorPos + 1);

            // 存在確認
            if (!checkExist(rtcName, portName)) {
                throw new RuntimeException("Port Not Found. RTC-Name:" + rtcName + ", Port-Name:" + portName);
            }

            portParam.setRtcName(rtcName);
            portParam.setPortName(portName);
            return portParam;
        }

        // コロンが入っていない場合はポート名からRTC名を取得
        Map<String, List<String>> portKeyMap = getPortKeyMap();
        List<String> rtcNameList = portKeyMap.get(value);
        if (1 != rtcNameList.size()) {
            throw new RuntimeException("Port-Name Not Unique. Port-Name:" + value);
        }

        portParam.setPortName(value);
        portParam.setRtcName(rtcNameList.get(0));
        return portParam;
    }

    /**
     * RTC名とポート名の組み合わせが存在するかを確認する。
     * @param rtcName  RTC名。
     * @param portName ポート名。
     * @return チェック結果。
     */
    private boolean checkExist(String rtcName, String portName) {

        List<PortInfo> portList = dao.getPortMap().get(rtcName);
        if (null == portList) {
            return false;
        }

        for (PortInfo port : portList) {
            if (portName.equals(port.getName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 取り得る値を列挙する。
     * @inheritDoc
     */
    @Override
    public boolean getAllPossibleValues(List<Completion> completions, Class<?> targetType, String existingData,
            String optionContext, MethodTarget target) {

        // ポート名をキーとしたマップを取得
        Map<String, List<String>> portKeyMap = getPortKeyMap();
        for (String portName : portKeyMap.keySet()) {

            List<String> rtcNameList = portKeyMap.get(portName);

            // ポート名の重複がない場合はポート名を設定
            if (1 == rtcNameList.size()) {
                completions.add(new Completion(portName));
                continue;
            }

            // ポート名の重複がある場合は「RTC名:ポート名」を設定
            for (String rtcName : rtcNameList) {
                completions.add(new Completion(rtcName + ShellConsts.PORT_SEPARATOR + portName));
            }
        }

        return false;
    }

    /**
     * key：ポート名、value：RTC名のマップを返す。
     * @return マップ。
     */
    private Map<String, List<String>> getPortKeyMap() {

        // ポート名をキーにしたマップ
        Map<String, List<String>> portKeyMap = new TreeMap<>();

        // ポート名の重複を判断できるようにマップに格納
        Map<String, List<PortInfo>> portMap = dao.getPortMap();
        for (String rtcName : portMap.keySet()) {
            List<PortInfo> portList = portMap.get(rtcName);
            for (PortInfo port : portList) {
                if (!port.isInPort()) {
                    String portName = port.getName();
                    if (portKeyMap.containsKey(portName)) {
                        portKeyMap.get(portName).add(rtcName);
                    } else {
                        portKeyMap.put(portName, Arrays.asList(rtcName));
                    }
                }
            }
        }

        return portKeyMap;
    }

}
