-- ------查询需要将小写改成大写的字段
SELECT concat(
               'alter table ',
               'tg_tag_sticker_data',
               ' change column ',
               COLUMN_NAME,
               ' ',
               UPPER(COLUMN_NAME),
               ' ',
               COLUMN_TYPE,
               ' comment \'',
               COLUMN_COMMENT,
               '\';'
           ) AS '修改脚本'
FROM information_schema.COLUMNS
WHERE TABLE_NAME = 'tg_tag_sticker_data';

alter table met_db_tab change column id ID bigint(20) comment '主键ID';
alter table met_db_tab change column db_name DB_NAME varchar (200) comment '对应的实体表的名称';
alter table met_db_tab change column tab_name TAB_NAME varchar (200) comment '表名称';
alter table met_db_tab change column alias ALIAS varchar (100) comment '表名别名，简写';
alter table met_db_tab change column remark REMARK varchar (255) comment '备注信息';

alter table met_event change column id ID bigint comment '';
alter table met_event change column project PROJECT varchar (100) comment '项目名称(en编码）';
alter table met_event change column name NAME varchar (255) comment '事件名(英文名)';
alter table met_event change column cname CNAME varchar (255) comment '事件显示名(中文名称)';
alter table met_event change column reported REPORTED bit (1) comment '是否上报数据,1=是';
alter table met_event change column visible VISIBLE bit (1) comment '是否可见，1=是';
alter table met_event change column received RECEIVED bit (1) comment '是否接收，1=是';
alter table met_event change column platform PLATFORM varchar (200) comment '应埋点平台，用逗号隔开';
alter table met_event change column tag TAG varchar (200) comment '标签';
alter table met_event change column db_tab_name DB_TAB_NAME varchar (200) comment '数据库表格名称';
alter table met_event change column create_by CREATE_BY bigint comment '创建人';
alter table met_event change column create_time CREATE_TIME datetime comment '创建时间';
alter table met_event change column update_by UPDATE_BY bigint comment '修改人';
alter table met_event change column update_time UPDATE_TIME datetime comment '修改时间';
alter table met_event change column remark REMARK varchar (255) comment '备注信息';

alter table met_event_field change column id ID bigint comment '主键id';
alter table met_event_field change column pid PID bigint comment '父id';
alter table met_event_field change column name NAME varchar (100) comment '名称';
alter table met_event_field change column field FIELD varchar (50) comment '字段名';
alter table met_event_field change column aggregator AGGREGATOR varchar (50) comment '聚合函数';
alter table met_event_field change column unit_name UNIT_NAME varchar (100) comment '计量单位名称';
alter table met_event_field change column status STATUS tinyint comment '状态';

alter table met_property change column id ID bigint comment '';
alter table met_property change column project PROJECT varchar (100) comment '项目名称(en编码）';
alter table met_property change column event_id EVENT_ID bigint comment '事件id';
alter table met_property change column name NAME varchar (200) comment '属性名';
alter table met_property change column cname CNAME varchar (200) comment '属性显示名';
alter table met_property change column data_type DATA_TYPE varchar (20) comment '数据类型';
alter table met_property change column reported REPORTED bit (1) comment '是否上报数据,1=是';
alter table met_property change column is_preset IS_PRESET bit (1) comment '是否预置';
alter table met_property change column web_widget WEB_WIDGET varchar (200) comment 'PC端页面组件名称';
alter table met_property change column table_name TABLE_NAME varchar (200) comment '该字段对应的表名称';
alter table met_property change column db_column DB_COLUMN varchar (100) comment '数据库字段';
alter table met_property change column has_dict HAS_DICT bit (1) comment '是否有字典值';
alter table met_property change column status STATUS tinyint comment '属性状态，1=可用';
alter table met_property change column visible VISIBLE bit (1) comment '是否可见，1=是';
alter table met_property change column type TYPE varchar (30) comment '属性类型，USER=用户属性，EVENT=事件属性';
alter table met_property change column create_by CREATE_BY bigint comment '创建人';
alter table met_property change column create_time CREATE_TIME datetime comment '创建时间';
alter table met_property change column update_by UPDATE_BY bigint comment '修改人';
alter table met_property change column update_time UPDATE_TIME datetime comment '修改时间';
alter table met_property change column remark REMARK varchar (255) comment '备注信息';


alter table met_property_value change column id ID bigint comment '主键id';
alter table met_property_value change column property_id PROPERTY_ID bigint comment '属性id';
alter table met_property_value change column name NAME varchar (200) comment '属性名称';
alter table met_property_value change column value VALUE varchar (200) comment '属性值';
alter table met_property_value change column create_by CREATE_BY bigint comment '创建人';
alter table met_property_value change column create_time CREATE_TIME datetime comment '创建时间';
alter table met_property_value change column update_by UPDATE_BY bigint comment '修改人';
alter table met_property_value change column update_time UPDATE_TIME datetime comment '修改时间';
alter table met_property_value change column remark REMARK varchar (255) comment '备注信息';

alter table met_relation change column id ID bigint comment '';
alter table met_relation change column type TYPE varchar (20) comment '符号类型';
alter table met_relation change column name NAME varchar (100) comment '名称';
alter table met_relation change column function FUNCTION varchar (20) comment '连接函数';
alter table met_relation change column symbol SYMBOL varchar (20) comment '函数对应的连接符号';
alter table met_relation change column status STATUS tinyint comment '是否有效,1=有效';
alter table met_relation change column data_type_suitable DATA_TYPE_SUITABLE varchar (100) comment '该符号适合的数据类型';
alter table met_relation change column web_widget WEB_WIDGET varchar (200) comment 'PC端页面组件名称';

alter table tg_tag change column id ID bigint comment '主键id';
alter table tg_tag change column pid PID bigint comment '父标签id';
alter table tg_tag change column name NAME varchar (200) comment '标签英文名';
alter table tg_tag change column cname CNAME varchar (255) comment '标签中文名';
alter table tg_tag change column tag_group_id TAG_GROUP_ID bigint comment '标签类型/分组ID';
alter table tg_tag change column status STATUS tinyint comment '标签状态,1=可用';
alter table tg_tag change column is_routine IS_ROUTINE bit (1) comment '是否例行执行';
alter table tg_tag change column cron_express CRON_EXPRESS varchar (50) comment '定时执行corn表达式';
alter table tg_tag change column base_time BASE_TIME datetime comment '标签计算的基准时间';
alter table tg_tag change column unit UNIT varchar (20) comment '标签更新周期，DAY=按天，HOUR=按小时，WEEK=按周';
alter table tg_tag change column data_type DATA_TYPE varchar (20) comment '标签数据类型，BOOL,NUMBER,STRING,DATETIME,LIST';
alter table tg_tag change column source_type SOURCE_TYPE tinyint comment '标签类型，1--自定义标签值，2--基础指标值，3--首次末次特征，4--事件偏好属性，5--行为分布结果，6--sql创建';
alter table tg_tag change column access_permission ACCESS_PERMISSION tinyint comment '权限设置，1=仅自己可见，2=所有成员可见，3=指定成员可见';
alter table tg_tag change column rule_content RULE_CONTENT text comment '规则内容';
alter table tg_tag change column sort SORT int comment '排序';
alter table tg_tag change column cname_color CNAME_COLOR varchar (50) comment '文字颜色';
alter table tg_tag change column create_by CREATE_BY bigint comment '创建人';
alter table tg_tag change column create_time CREATE_TIME datetime comment '创建时间';
alter table tg_tag change column update_by UPDATE_BY bigint comment '修改人';
alter table tg_tag change column update_time UPDATE_TIME datetime comment '修改时间';
alter table tg_tag change column remark REMARK varchar (255) comment '备注信息';

alter table tg_tag_data change column id ID bigint(20) comment '主键ID';
alter table tg_tag_data change column tag_id TAG_ID bigint(20) comment '标签id';
alter table tg_tag_data change column rule_content RULE_CONTENT text comment '规则内容';
alter table tg_tag_data change column sql_content SQL_CONTENT text comment '查询的sql语句';
alter table tg_tag_data change column base_time BASE_TIME datetime comment '标签计算的基准时间';
alter table tg_tag_data change column begin_time BEGIN_TIME datetime comment '开始计算时间';
alter table tg_tag_data change column calc_time CALC_TIME datetime comment '计算完成时间';
alter table tg_tag_data change column total TOTAL bigint(20) comment '统计总数';
alter table tg_tag_data change column calc_status CALC_STATUS tinyint(2) comment '计算状态,0=未开始计算，1=计算完成，2=计算中,3=计算出错,4=延迟计算';

alter table tg_tag_dimension change column id ID bigint(20) comment '';
alter table tg_tag_dimension change column tag_id TAG_ID bigint(20) comment '标签id';
alter table tg_tag_dimension change column property_id PROPERTY_ID bigint(20) comment '属性id';
alter table tg_tag_dimension change column name NAME varchar (200) comment '维度名称';
alter table tg_tag_dimension change column sort SORT int (3) comment '维度排序';
alter table tg_tag_dimension change column create_by CREATE_BY bigint(20) comment '创建人';
alter table tg_tag_dimension change column create_time CREATE_TIME datetime comment '创建时间';
alter table tg_tag_dimension change column update_by UPDATE_BY bigint(20) comment '修改人';
alter table tg_tag_dimension change column update_time UPDATE_TIME datetime comment '修改时间';
alter table tg_tag_dimension change column remark REMARK varchar (255) comment '备注信息';


alter table tg_tag_dimension_data change column id ID bigint(20) comment '主键ID';
alter table tg_tag_dimension_data change column his_tag_id HIS_TAG_ID bigint(20) comment '历史数据标签ID';
alter table tg_tag_dimension_data change column dimension_id DIMENSION_ID bigint(20) comment '维度id';
alter table tg_tag_dimension_data change column name NAME varchar (200) comment '维度名称';
alter table tg_tag_dimension_data change column sort SORT int (3) comment '维度排序';
alter table tg_tag_dimension_data change column remark REMARK varchar (255) comment '备注信息';
alter table tg_tag_dimension_data change column total TOTAL bigint(20) comment '统计总数';
alter table tg_tag_dimension_data change column property_id PROPERTY_ID bigint(20) comment '属性id';

alter table tg_tag_dimension_rec change column id ID bigint(20) comment '';
alter table tg_tag_dimension_rec change column dimension_id DIMENSION_ID bigint(20) comment '维度id';
alter table tg_tag_dimension_rec change column tag_id TAG_ID bigint(20) comment '标签id';
alter table tg_tag_dimension_rec change column name NAME varchar (200) comment '维度名称';
alter table tg_tag_dimension_rec change column sort SORT int (3) comment '维度排序';
alter table tg_tag_dimension_rec change column create_by CREATE_BY bigint(20) comment '创建人';
alter table tg_tag_dimension_rec change column create_time CREATE_TIME datetime comment '创建时间';
alter table tg_tag_dimension_rec change column update_by UPDATE_BY bigint(20) comment '修改人';
alter table tg_tag_dimension_rec change column update_time UPDATE_TIME datetime comment '修改时间';
alter table tg_tag_dimension_rec change column remark REMARK varchar (255) comment '备注信息';
alter table tg_tag_dimension_rec change column version VERSION int (5) comment '版本号';

alter table tg_tag_group change column id ID bigint(20) comment '';
alter table tg_tag_group change column pid PID bigint(20) comment '父标签分组id';
alter table tg_tag_group change column name NAME varchar (200) comment '标签分类名称';
alter table tg_tag_group change column type TYPE tinyint(1) comment '标签分类,1=规则标签，2=贴纸标签';
alter table tg_tag_group change column status STATUS tinyint(2) comment '状态（1正常，0停用）';
alter table tg_tag_group change column sort SORT int (5) comment '标签排序';
alter table tg_tag_group change column create_by CREATE_BY bigint(20) comment '创建人';
alter table tg_tag_group change column create_time CREATE_TIME datetime comment '创建时间';
alter table tg_tag_group change column update_by UPDATE_BY bigint(20) comment '修改人';
alter table tg_tag_group change column update_time UPDATE_TIME datetime comment '修改时间';
alter table tg_tag_group change column remark REMARK varchar (512) comment '备注';


alter table tg_tag_layer change column id ID bigint(20) comment '';
alter table tg_tag_layer change column dimension_id DIMENSION_ID bigint(20) comment '标签id';
alter table tg_tag_layer change column field FIELD varchar (30) comment '字段名称';
alter table tg_tag_layer change column function FUNCTION varchar (20) comment '连接函数';
alter table tg_tag_layer change column params PARAMS varchar (200) comment '参数';
alter table tg_tag_layer change column show_name SHOW_NAME varchar (100) comment '显示名称';
alter table tg_tag_layer change column sort SORT int (5) comment '分层顺序';
alter table tg_tag_layer change column create_time CREATE_TIME datetime comment '创建时间';
alter table tg_tag_layer change column update_time UPDATE_TIME datetime comment '修改时间';
alter table tg_tag_layer change column remark REMARK varchar (255) comment '备注信息';
alter table tg_tag_layer change column web_widget WEB_WIDGET varchar (100) comment '网页控件';

alter table tg_tag_layer_data change column id ID bigint(20) comment '';
alter table tg_tag_layer_data change column layer_id LAYER_ID bigint(20) comment '分层id';
alter table tg_tag_layer_data change column his_dimension_id HIS_DIMENSION_ID bigint(20) comment '维度id';
alter table tg_tag_layer_data change column field FIELD varchar (30) comment '字段名称';
alter table tg_tag_layer_data change column function FUNCTION varchar (20) comment '连接函数';
alter table tg_tag_layer_data change column params PARAMS varchar (200) comment '参数';
alter table tg_tag_layer_data change column sort SORT int (5) comment '分层顺序';
alter table tg_tag_layer_data change column show_name SHOW_NAME varchar (100) comment '显示名称';
alter table tg_tag_layer_data change column num NUM bigint(20) comment '统计数量';
alter table tg_tag_layer_data change column percent PERCENT decimal (10,8) comment '百分比';
alter table tg_tag_layer_data change column web_widget WEB_WIDGET varchar (100) comment '网页控件';

alter table tg_tag_layer_rec change column id ID bigint(20) comment '';
alter table tg_tag_layer_rec change column layer_id LAYER_ID bigint(20) comment '分层id';
alter table tg_tag_layer_rec change column dimension_id DIMENSION_ID bigint(20) comment '标签id';
alter table tg_tag_layer_rec change column field FIELD varchar (30) comment '字段名称';
alter table tg_tag_layer_rec change column function FUNCTION varchar (20) comment '连接函数';
alter table tg_tag_layer_rec change column params PARAMS varchar (200) comment '参数';
alter table tg_tag_layer_rec change column sort SORT int (5) comment '分层顺序';
alter table tg_tag_layer_rec change column create_time CREATE_TIME datetime comment '创建时间';
alter table tg_tag_layer_rec change column update_time UPDATE_TIME datetime comment '修改时间';
alter table tg_tag_layer_rec change column remark REMARK varchar (255) comment '备注信息';
alter table tg_tag_layer_rec change column version VERSION int (5) comment '版本号';
alter table tg_tag_layer_rec change column web_widget WEB_WIDGET varchar (100) comment '网页控件';

alter table tg_tag_rec change column id ID bigint(20) unsigned comment '主键id';
alter table tg_tag_rec change column tag_id TAG_ID bigint(20) comment '标签id';
alter table tg_tag_rec change column pid PID bigint(20) comment '父标签id';
alter table tg_tag_rec change column name NAME varchar (200) comment '标签英文名';
alter table tg_tag_rec change column cname CNAME varchar (255) comment '标签中文名';
alter table tg_tag_rec change column tag_group_id TAG_GROUP_ID bigint(20) comment '标签类型/分组ID';
alter table tg_tag_rec change column status STATUS tinyint(2) comment '标签状态';
alter table tg_tag_rec change column is_routine IS_ROUTINE bit (1) comment '是否例行执行';
alter table tg_tag_rec change column cron_express CRON_EXPRESS varchar (50) comment '定时执行corn表达式';
alter table tg_tag_rec change column base_time BASE_TIME datetime comment '标签计算的基准时间';
alter table tg_tag_rec change column unit UNIT varchar (20) comment '标签更新周期，DAY=按天，HOUR=按小时，WEEK=按周';
alter table tg_tag_rec change column data_type DATA_TYPE varchar (20) comment '标签数据类型，BOOL,NUMBER,STRING,DATETIME,LIST';
alter table tg_tag_rec change column source_type SOURCE_TYPE tinyint(2) comment '标签类型，1--自定义标签值，2--基础指标值，3--首次末次特征，4--事件偏好属性，5--行为分布结果，6--sql创建';
alter table tg_tag_rec change column access_permission ACCESS_PERMISSION tinyint(2) comment '权限设置，1=仅自己可见，2=所有成员可见，3=指定成员可见';
alter table tg_tag_rec change column rule_content RULE_CONTENT text comment '规则内容';
alter table tg_tag_rec change column sort SORT int (5) comment '排序';
alter table tg_tag_rec change column cname_color CNAME_COLOR varchar (50) comment '文字颜色';
alter table tg_tag_rec change column create_by CREATE_BY bigint(20) comment '创建人';
alter table tg_tag_rec change column create_time CREATE_TIME datetime comment '创建时间';
alter table tg_tag_rec change column update_by UPDATE_BY bigint(20) comment '修改人';
alter table tg_tag_rec change column update_time UPDATE_TIME datetime comment '修改时间';
alter table tg_tag_rec change column remark REMARK varchar (255) comment '备注信息';
alter table tg_tag_rec change column version VERSION int (5) comment '版本号';

alter table tg_tag_sticker change column id ID bigint(20) comment '';
alter table tg_tag_sticker change column name NAME varchar (200) comment '贴纸标签名称';
alter table tg_tag_sticker change column cname CNAME varchar (255) comment '标签中文名';
alter table tg_tag_sticker change column tag_group_id TAG_GROUP_ID bigint(20) comment '标签类型/分组ID';
alter table tg_tag_sticker change column status STATUS tinyint(2) comment '标签状态';
alter table tg_tag_sticker change column access_permission ACCESS_PERMISSION tinyint(2) comment '权限设置，1=仅自己可见，2=所有成员可见，3=指定成员可见';
alter table tg_tag_sticker change column sort SORT int (5) comment '分层顺序';
alter table tg_tag_sticker change column cname_color CNAME_COLOR varchar (50) comment '文字颜色';
alter table tg_tag_sticker change column create_by CREATE_BY bigint(20) comment '创建人';
alter table tg_tag_sticker change column create_time CREATE_TIME datetime comment '创建时间';
alter table tg_tag_sticker change column update_by UPDATE_BY bigint(20) comment '修改人';
alter table tg_tag_sticker change column update_time UPDATE_TIME datetime comment '修改时间';
alter table tg_tag_sticker change column remark REMARK varchar (255) comment '备注信息，说明';

alter table tg_tag_sticker_data change column id ID bigint(20) comment '';
alter table tg_tag_sticker_data change column tag_sticker_id TAG_STICKER_ID bigint(20) comment '贴纸标签ID';
alter table tg_tag_sticker_data change column total TOTAL bigint(20) comment '统计总数';
alter table tg_tag_sticker_data change column base_time BASE_TIME datetime comment '标签计算的基准时间';
alter table tg_tag_sticker_data change column calc_time CALC_TIME datetime comment '计算完成时间';
alter table tg_tag_sticker_data change column calc_status CALC_STATUS tinyint(2) comment '计算状态,0=未开始计算，1=计算完成，2=计算中,3=计算出错,4=延迟计算';
alter table tg_tag_sticker_data change column show_name SHOW_NAME varchar (100) comment '图表中显示名称';
alter table tg_tag_sticker_data change column num NUM bigint(20) comment '统计数量';
alter table tg_tag_sticker_data change column percent PERCENT decimal (10,8) comment '百分比，小数';

ALTER TABLE `tg_tag_rec`
    MODIFY COLUMN `ID`  bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键id' FIRST ;

ALTER TABLE `tg_tag_dimension_rec`
    MODIFY COLUMN `ID`  bigint(20) NOT NULL AUTO_INCREMENT FIRST ;

ALTER TABLE `tg_tag_layer_rec`
    MODIFY COLUMN `ID`  bigint(20) NOT NULL AUTO_INCREMENT FIRST ;


-- ---已更新-----
















