package jp.co.humane.rtc.tool.connector.shell.command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.CommandMarker;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.stereotype.Component;

import com.google.common.base.Objects;

import jp.co.humane.rtc.tool.connector.component.ConnectProfileHolder;
import jp.co.humane.rtc.tool.connector.shell.command.table.ConnectProfileShowBean;
import jp.co.humane.rtc.tool.connector.shell.common.TableBuilderUtil;

/**
 * connect-profileコマンドの処理を定義。
 * @author teradakng
 *
 */
@Component
public class ConnectProfileCommand implements CommandMarker {

    /** キーと値のセパレータ */
    private static final String KEY_VALUE_SEPARATOR = "=";

    /** リストのセパレータ */
    private static final String LIST_SEPARATOR = ",";

    /** 接続設定保持クラス */
    @Autowired
    private ConnectProfileHolder profileHolder = null;

    @CliCommand(value = "connect-profile", help = "Connect Profile Setting")
    public String simple(
            @CliOption(key = { "show" }, mandatory = false, help = "Show All Profiles", unspecifiedDefaultValue = " ") final String show,
            @CliOption(key = { "restoreDefault" }, mandatory = false, help = "Restore Default Profiles", unspecifiedDefaultValue = " ") final String restoreDefault,
            @CliOption(key = { "addProfile" }, mandatory = false, help = "Add Profile With Format key=value", unspecifiedDefaultValue = "") final String addProfile,
            @CliOption(key = { "removeProfile" }, mandatory = false, help = "Remove Profile With Format key", unspecifiedDefaultValue = "") final String removeProfile,
            @CliOption(key = { "addProfileList" }, mandatory = false, help = "Add Profile With Format key=value,key=value,...", unspecifiedDefaultValue = "") final String addProfileList,
            @CliOption(key = { "removeProfileList" }, mandatory = false, help = "Remove Profiles With Format key,key,...", unspecifiedDefaultValue = "") final String removeProfileList
            ) {

        String msg = null;

        if (!Objects.equal(show, " ")) {
            msg = showProfiles();
        } else if (!Objects.equal(show, " ")) {
            msg = restoreDefault();
        } else if (!StringUtils.isEmpty(addProfile)) {
            msg = addProfile(addProfile);
        } else if (!StringUtils.isEmpty(removeProfile)) {
            msg = removeProfile(removeProfile);
        } else if (!StringUtils.isEmpty(addProfileList)) {
            msg = addProfileList(addProfileList);
        } else if (!StringUtils.isEmpty(removeProfileList)) {
            msg = removeProfileList(removeProfileList);
        } else {
            msg = "操作を指定してください。";
        }


        return msg;
    }

    /**
     * 現在の接続設定を表示する。
     * @return 設定情報。
     */
    private String showProfiles() {

        // 接続情報を表示出力用Beanに格納
        List<ConnectProfileShowBean> beanList = new ArrayList<>();
        Map<String, String> profileMap = profileHolder.getProfiles();
        profileMap.forEach((key, value) -> {
            ConnectProfileShowBean bean = new ConnectProfileShowBean();
            bean.setKey(key);
            bean.setValue(value);
            beanList.add(bean);
        });

        // Beanリストをテーブル形式に変換
        Collections.sort(beanList);
        return TableBuilderUtil.render(beanList, ConnectProfileShowBean.HEADER);
    }

    /**
     * デフォルト設定を復元する。
     * @return 設定結果。
     */
    private String restoreDefault() {
        profileHolder.restoreDefault();
        return "デフォルト設定を復元しました。";
    }

    /**
     * 設定を追加する。
     * @param profile 設定情報。
     * @return 設定結果。
     */
    private String addProfile(String profile) {

        String[] splitProfiles = StringUtils.split(profile, KEY_VALUE_SEPARATOR);
        if (splitProfiles.length != 2) {
            return "入力値が不正です。key=valueの形式で指定してください。";
        }

        String key = splitProfiles[0];
        String value = splitProfiles[1];
        Map<String, String> profileMap =  profileHolder.getProfiles();
        if (profileMap.containsKey(key)) {
            profileMap.remove(key);
        }
        profileMap.put(key, value);

        return "設定を追加しました。";
    }

    /**
     * 設定を削除する。
     * @param key キー値。
     * @return 削除結果。
     */
    private String removeProfile(String key) {

        Map<String, String> profileMap =  profileHolder.getProfiles();
        if (!profileMap.containsKey(key)) {
            return key + "は設定されていないため削除できません。";
        }
        profileMap.remove(key);

        return "設定を削除しました。";
    }

    /**
     * 複数の設定を追加する。
     * @param profs 設定情報。
     * @return 追加結果。
     */
    private String addProfileList(String profs) {

        // key=valueの形式になっていることをチェックする
        List<String[]> profList =  Stream.of(StringUtils.split(profs, LIST_SEPARATOR))
                                          .map(keyValue -> StringUtils.split(keyValue, KEY_VALUE_SEPARATOR))
                                          .collect(Collectors.toList());
        for (String[] arr : profList) {
            if (arr.length != 2) {
                return "入力値が不正です。key=value,key=valeu,...の形式で指定してください。";
            }
        }

        Map<String, String> profileMap =  profileHolder.getProfiles();
        for (String[] arr : profList) {
            String key = arr[0];
            String value = arr[1];
            if (profileMap.containsKey(key)) {
                profileMap.remove(key);
            }
            profileMap.put(key, value);
        }

        return "設定を追加しました。";
    }

    /**
     * 複数の設定を削除する。
     * @param profs 設定のキー情報。
     * @return  削除結果。
     */
    private String removeProfileList(String profs) {

        // 指定のキー情報が設定されていることを確認する
        String[] keys = StringUtils.split(profs, LIST_SEPARATOR);
        Map<String, String> profileMap =  profileHolder.getProfiles();
        for (String key : keys) {
            if (!profileMap.containsKey(key)) {
                return key + "は設定されていないため削除できません。";
            }
        }

        // 削除を実施
        for (String key : keys) {
            profileMap.remove(key);
        }

        return "設定を削除しました。";
    }

}
