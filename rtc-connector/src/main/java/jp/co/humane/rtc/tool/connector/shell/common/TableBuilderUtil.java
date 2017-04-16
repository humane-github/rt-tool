/**
 *
 */
package jp.co.humane.rtc.tool.connector.shell.common;

import java.util.LinkedHashMap;
import java.util.List;

import org.springframework.shell.table.BeanListTableModel;
import org.springframework.shell.table.BorderSpecification;
import org.springframework.shell.table.BorderStyle;
import org.springframework.shell.table.TableBuilder;

/**
 * RTC Connector用のTableBuilder
 * @author terada
 *
 */
public class TableBuilderUtil {

    /** コンソールの横幅 */
    public static final int RENDER_SIZE = 130;

    /**
     * リストをテーブル形式の文字列に変換する。
     * @param list   bean一覧。
     * @param header ヘッダ情報。
     * @return テーブル文字列。
     */
    public static <T> String render(List<T> list, LinkedHashMap<String, Object> header) {

        return new TableBuilder(new BeanListTableModel<T>(list, header))
                .paintBorder(BorderStyle.air, BorderSpecification.INNER_VERTICAL).fromTopLeft().toBottomRight()
                .build()
                .render(RENDER_SIZE);

    }

}
