package com.kuangheng.cloud.tag.constant;

/**
 * 规则类型
 */
public enum RuleType {

    RULES_RELATION("rules_relation", "规则类型"),
    PROFILE_RULE("profile_rule", "用户属性"),
    EVENT_RULE("event_rule", "事件规则"),
    EVENT_MEASURE("event_measure", "事件度量"),
    EVENT_SEQUENCE_RULE("event_sequence_rule", "事件规则"),
    ;

    private String code;

    private String name;

    RuleType(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
