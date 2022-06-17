ALTER TABLE `tg_tag`
    ADD COLUMN `SQL_CONTENT`  text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '解析后的查询的sql语句' AFTER `RULE_CONTENT`;

-------已更新------

create table dev_dwd_zinger_relation.customer_group_fieldval
(
    ID          BIGINT(19),
    CUSTOMER_GROUP_ID BIGINT(19),
    FIELD_VAL STRING(32767)
);

comment on table dev_dwd_zinger_relation.customer_group_fieldval is '客户群组导入创建关联数据';


create table dev_dwd_zinger_relation.data_customer_group
(
    ID          BIGINT(19),
    CUSTOMER_GROUP_ID BIGINT(19),
    CUSTOMER_ID BIGINT(19)
);

comment on table dev_dwd_zinger_relation.data_customer_group is '客户群组计算数据';
