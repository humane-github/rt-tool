/**
 *
 */
package jp.co.humane.rtc.tool.connector.common.enums;

import RTC.LifeCycleState;

/**
 * コンポーネントの状態を表す列挙型。
 * @author terada.
 *
 */
public enum ComponentState {

    /** 生成状態 */
    CREATED(LifeCycleState._CREATED_STATE),

    /** 非活性状態 */
    INACTIVE(LifeCycleState._INACTIVE_STATE),

    /** 活性状態 */
    ACTIVE(LifeCycleState._ACTIVE_STATE),

    /** エラー状態 */
    ERROR(LifeCycleState._ERROR_STATE);

    /** 状態を表す数値 */
    private int value = 0;

    /**
     * コンストラクタ。
     * @param value 状態を表す数値。
     */
    private ComponentState(int value) {
        this.value = value;
    }

    /**
     * 状態を表す数値からComponentState型を取得する。
     * @param value 状態を表す数値。
     * @return ComponentState.
     */
    public static ComponentState get(int value) {
        for (ComponentState state : ComponentState.values()) {
            if (state.value == value) {
                return state;
            }
        }
        return null;
    }
}
