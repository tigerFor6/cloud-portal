ALTER TABLE `met_relation`
    ADD COLUMN `web_widget` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'PC端页面组件名称' AFTER `data_type_suitable`;

ALTER TABLE `met_event`
    ADD COLUMN `db_tab_name` varchar(200) NULL COMMENT '数据库表格名称' AFTER `tag`;

ALTER TABLE `tg_tag_dimension`
    ADD COLUMN `property_id` bigint(20) NULL COMMENT '属性id' AFTER `tag_id`;

ALTER TABLE `met_property`
    ADD COLUMN `table_name` varchar(100) NULL COMMENT '数据库表名';

-- --------已经更新完成------------------

---------20210422新增------------
ALTER TABLE `tg_tag_layer`
    ADD COLUMN `show_name` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '显示名称' AFTER `params`;

ALTER TABLE `tg_tag_his`
    ADD UNIQUE INDEX `uni-tag_id-base_time` (`tag_id`, `base_time`),
    AUTO_INCREMENT=1384250793540063233;



ALTER TABLE `tg_tag_dimension_his`
    ADD UNIQUE INDEX `uni-his_tag_id-dim_id` (`his_tag_id`, `dimension_id`) COMMENT '历史标签id和维度保证唯一';


---------已更新--



