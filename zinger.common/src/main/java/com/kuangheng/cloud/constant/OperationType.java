package com.kuangheng.cloud.constant;

/**
 * @Author Carlos.Chen
 * @Date 2020/12/29
 */
public enum OperationType {
    /**
     * 操作类型
     */
    UNKNOWN("0"),
    SELECT("1"),
    INSERT("2"),
    UPDATE("3"),
    DELETE("4"),
    REMOVE("5");

    private String value;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    OperationType(String s) {
        this.value = s;
    }

}
