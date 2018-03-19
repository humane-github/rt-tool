/**
 *
 */
package jp.co.humane.rtc.tool.connector.shell.command.arg.converter;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.Completion;
import org.springframework.shell.core.Converter;
import org.springframework.shell.core.MethodTarget;
import org.springframework.stereotype.Component;

import jp.co.humane.rtc.tool.connector.dao.NamingServerDao;
import jp.co.humane.rtc.tool.connector.shell.command.arg.ConfigParam;

/**
 * コンフィグパラメータのコンバータ。
 * @author terada.
 *
 */
@Component
public class ConfigConverter  implements Converter<ConfigParam> {

    /** RTC名を取得する正規表現 */
    private static final Pattern PATTERN_RTC_NAME = Pattern.compile("--rtc\\s+([^\\s]+)\\s");

    /** ネーミングサーバDao */
    @Autowired
    private NamingServerDao dao = null;

    /**
     * サポートする型を設定する。
     * @inheritDoc
     */
    @Override
    public boolean supports(Class<?> type, String optionContext) {
        return ConfigParam.class.isAssignableFrom(type);    }

    /**
     * 文字列からコンフィグパラメータを生成する。
     * @inheritDoc
     */
    @Override
    public ConfigParam convertFromText(String value, Class<?> targetType, String optionContext) {

        ConfigParam cp = new ConfigParam();
        cp.setName(value);
        return cp;
    }

    /**
     * 取り得る値を列挙する。
     * @inheritDoc
     */
    @Override
    public boolean getAllPossibleValues(List<Completion> completions, Class<?> targetType, String existingData,
            String optionContext, MethodTarget target) {

        // RTC名が取得できない場合は候補を表示しない
        Matcher matcher = PATTERN_RTC_NAME.matcher(target.getRemainingBuffer());
        if (!matcher.find()) {
            return true;
        }

        // 存在しないRTC名の場合は候補を表示しない
        String rtcName = matcher.group(1);
        Map<String, Map<String, String>> allConfigMap = dao.getConfigMap();
        if (!allConfigMap.containsKey(rtcName)) {
            return true;
        }

        // configのパラメータ一覧を候補として返す
        Map<String, String> configMap = allConfigMap.get(rtcName);
        for (String key : configMap.keySet()) {
            completions.add(new Completion(key));
        }

        return true;
    }

}
