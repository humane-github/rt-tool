/**
 *
 */
package jp.co.humane.rtc.tool.connector.shell.command.arg.converter;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.Completion;
import org.springframework.shell.core.Converter;
import org.springframework.shell.core.MethodTarget;
import org.springframework.stereotype.Component;

import jp.co.humane.rtc.tool.connector.dao.NamingServerDao;
import jp.co.humane.rtc.tool.connector.dto.RtcInfo;
import jp.co.humane.rtc.tool.connector.shell.command.arg.RtcParam;

/**
 * RTCパラメータのコンバータ。
 * @author terada.
 *
 */
@Component
public class RtcConverter implements Converter<RtcParam> {

    /** ネーミングサーバDao */
    @Autowired
    private NamingServerDao dao = null;

    /**
     * サポートする型を設定する。
     * @inheritDoc
     */
    @Override
    public boolean supports(Class<?> type, String optionContext) {
        return RtcParam.class.isAssignableFrom(type);    }

    /**
     * 文字列からポートパラメータを生成する。
     * @inheritDoc
     */
    @Override
    public RtcParam convertFromText(String value, Class<?> targetType, String optionContext) {
        RtcParam rtcParam = new RtcParam();
        rtcParam.setName(value);
        return rtcParam;
    }

    /**
     * 取り得る値を列挙する。
     * @inheritDoc
     */
    @Override
    public boolean getAllPossibleValues(List<Completion> completions, Class<?> targetType, String existingData,
            String optionContext, MethodTarget target) {

        Set<String> portNameSet = new TreeSet<>();

        // RTC名の一覧を取得
        List<RtcInfo> rtcList = dao.getRtcList();
        Set<String> rtcSet = new TreeSet<>();
        for (RtcInfo rtc : rtcList) {
            rtcSet.add(rtc.getId());
        }

        // CompletionにRTC名を登録する
        for (String name : rtcSet) {
            completions.add(new Completion(name));
        }

        return false;
    }

}
