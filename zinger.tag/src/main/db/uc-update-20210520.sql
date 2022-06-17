ALTER TABLE `sys_org`
ADD COLUMN `LEADER`  bigint NULL COMMENT '负责人' AFTER `PARENT_ID`;
create index IDX_PARENT_ID on SYS_ORG(PARENT_ID);
create index IDX_STATUS on SYS_ORG(STATUS);


ALTER TABLE `sys_role`
COMMENT='角色表';
create index IDX_STATUS on SYS_ROLE(STATUS);

ALTER TABLE `sys_user`
COMMENT='用户账号表';
create index IDX_NAME on SYS_USER(NAME);
create index IDX_PHONE on SYS_USER(PHONE);
create index IDX_EMAIL on SYS_USER(EMAIL);
create index IDX_EMP_ID on SYS_USER(EMP_ID);
create index IDX_ORGID on SYS_USER(ORG_ID);
create index IDX_ROLE_ID on SYS_USER(ROLE_ID);
create index IDX_STATUS on SYS_USER(STATUS);


ALTER TABLE `sys_user_role`
COMMENT='用户角色关系表';


create index IDX_USERID on SYS_USER_ROLE(USER_ID);
create index IDX_ROLEID on SYS_USER_ROLE(ROLE_ID);

/** 密码修改记录表 **/
drop table if exists SYS_PWD_HIS;
create table SYS_PWD_HIS (
	ID bigint comment '主键',
    USER_ID bigint comment '用户ID',
    OLD_PWD varchar(128) comment '旧密码',
    UPDATE_TIME datetime comment '更新时间',
    primary key(ID)
) comment '密码修改记录表';


drop table if exists SYS_PARAMETER;
create table SYS_PARAMETER(
	ID bigint comment '主键',
    NAME varchar(32) not null comment '参数Key',
    VALUE varchar(256) comment '参数Value',
    ROLE_ID bigint comment '角色私有',
    UPDATE_BY bigint comment '更新人',
    UPDATE_TIME datetime comment '更新时间',
    COMMENT varchar(1024) DEFAULT NULL comment '备注',
    primary key (ID)
) comment '系统参数表';
select * from SYS_PARAMETER;


drop table if exists SYS_LANG;
create table SYS_LANG (
	ID varchar(32) comment '语言代码',
    NAME varchar(64) comment '语言描述',
    primary key (ID)
) comment '语言代码表';
select * from SYS_LANG;


drop table if exists SYS_I18N;
create table SYS_I18N (
	ID bigint comment '主键',
    MODULE_ID varchar(64) not null unique comment '模块名称',
    RESOURCES longtext comment '语言资源JSON字符串',
    UPDATE_BY bigint,
    UPDATE_TIME datetime,
    primary key(ID)
) comment '国际语言资源表';
create index IDX_MODULEID on SYS_I18N(MODULE_ID);
select * from SYS_I18N;

drop table if exists SYS_ADMINMENU;
create table SYS_ADMINMENU (
	ROLE_ID bigint comment '角色',
    PERMISSIONS longtext comment '菜单目录JSON格式',
    UPDATE_BY bigint comment '更新人',
    UPDATE_TIME datetime comment '更新时间',
    primary key(ROLE_ID)
) comment '管理台菜单表';
select * from SYS_ADMINMENU ;

/** 前台菜单表 **/
drop table if exists SYS_MENU;
create table SYS_MENU (
	ID bigint comment '主键',
    NAME varchar(128) comment '名称',
    TYPE int default 0 comment '菜单类型（0=目录，1=插件，2=虚拟页面）',
    CONTENT varchar(1024) comment '内容',
    PARENT_ID bigint comment '父目录',
    PARAMETER	longtext comment '启动参数',
    CREATE_BY bigint comment '创建者',
    CREATE_TIME datetime comment '创建时间',
    UPDATE_BY bigint comment '更新者',
    UPDATE_TIME datetime comment '更新时间',
	PERSISTENT tinyint(1) comment '持久',
    ICON varchar(64) comment '图标',
    HIDDEN tinyint(1) comment '隐藏',
    STARTUP tinyint(1) comment '自启动',
    STATUS int default 1 comment '状态',
    ORDER_INDEX int default 0 comment '排序',
    primary key(ID)
) comment '前台菜单表';
select * from SYS_MENU;


/** 前台菜单角色权限表 **/
drop table if exists SYS_MENU_ROLE;
create table SYS_MENU_ROLE (
    MENU_ID bigint comment '菜单ID',
    ROLE_ID bigint comment '角色ID',
    primary key(MENU_ID, ROLE_ID)
) comment '前台菜单角色权限表';
create index IDX_MENUID on SYS_MENU_ROLE(MENU_ID);
create index IDX_ROLEID on SYS_MENU_ROLE(ROLE_ID);
select * from SYS_MENU_ROLE;


/** 登录历史表 **/
drop table if exists T_LOGIN_HIS;
create table T_LOGIN_HIS (
	ID varchar(64) comment 'JTI',
    USER_ID bigint NOT NULL comment '用户ID',
    LOGIN_TIME datetime comment '登录时间',
    REMOTE_IP varchar(32) comment '远端IP地址',
    CLIENT_ID varchar(32) comment '客户端类型',
    TOKEN text comment 'token值',
    primary key(ID)
) comment '登录历史表';
create index IDX_USERID on T_LOGIN_HIS(USER_ID);
select * from T_LOGIN_HIS;

/** 业务锁 **/
drop table if exists T_LOCK;
create table T_LOCK (
	ID bigint comment '主键',
    SERVICE varchar(64) comment '服务名称',
    MAIN_KEY varchar(64) comment '业务主凭据',
    SUB_KEY varchar(64) comment '业务副凭据',
    CREATE_BY char(6) comment '加锁人',
    CREATE_TIME datetime comment '加锁时间',
    primary key(ID),
    unique index(SERVICE ASC, MAIN_KEY ASC, SUB_KEY ASC)
) comment '业务虚拟锁';
select * from T_LOCK;


/** 通告消息表 **/
drop table if exists T_ANNOUNCEMENT;
create table T_ANNOUNCEMENT (
	ID bigint comment '主键',
    TITLE varchar(1024) comment '标题',
    CONTENT longtext comment '正文',
    UPDATE_BY char(6) comment '更新者',
    UPDATE_TIME datetime comment '更新时间',
    ACTIVE int default 0 comment '状态（0=草稿, 1=发布中, 2=已发布）', 
    primary key (ID)
) comment '系统通告表';
select * from T_ANNOUNCEMENT;

/** 动态交互模块目录表 **/
drop table if exists T_DIM_CATALOG;
create table T_DIM_CATALOG (
	ID bigint comment '主键',
    NAME varchar(32) comment '模块名称',
    PARENT_ID bigint comment '父目录',
    primary key (ID)
) comment '动态交互模块目录表';


/** 动态交互模块表 **/
drop table if exists T_DIM;
create table T_DIM (
	ID bigint comment '主键',
    IDA varchar(8) comment '模块识别码',
    NAME varchar(32) comment '模块名称',
    ALIAS varchar(16) comment '模块别名',
    CATALOG_ID bigint comment '所在目录',
    STATEFUL tinyint(1) default 0 comment '有无状态',
    THEME varchar(32) comment '皮肤',
    VERSION varchar(16) comment '版本号',
    COMMENT varchar(1024) comment '备注',
    primary key (ID)
) comment '动态交互模块表';
create index IDX_IDA on T_DIM(IDA);
create index IDX_ALIAS on T_DIM(ALIAS);
create index IDX_CATALOGID on T_DIM(CATALOG_ID);


/** 数据维护目录表 **/
drop table if exists T_DBGRID_CATALOG;
create table T_DBGRID_CATALOG (
	ID bigint comment '主键',
    NAME varchar(128) comment '名称',
    PARENT_ID bigint comment '父目录',
    primary key(ID)
) comment '数据维护目录表';
select * from T_DBGRID_CATALOG;



/** 数据维护配置表 **/
drop table if exists T_DBGRID;
create table T_DBGRID (
	ID bigint comment '主键',
    CATALOG_ID bigint comment '父目录',
    NAME varchar(128) comment '名称',
    TABLE_NAME varchar(128) comment '表名',
    APPENDABLE tinyint(1) default 1 comment '可新增记录',
    EDITABLE tinyint(1) default 1 comment '可修改记录',
    REMOVABLE tinyint(1) default 1 comment '可删除记录',
    COMMENT varchar(512) DEFAULT NULL comment '备注',
    CREATE_BY_FIELD varchar(64) comment '创建者字段',
    CREATE_TIME_FIELD varchar(64) comment '创建时间字段',
    UPDATE_BY_FIELD varchar(64) comment '更新者字段',
    UPDATE_TIME_FIELD varchar(64) comment '更新时间字段',
    UPDATE_BY char(6) comment '创建者',
    UPDATE_TIME datetime comment '创建时间',
    primary key(ID)
) comment '数据维护配置表';
create index IDX_CATALOGID on T_DBGRID(CATALOG_ID);
select * from T_DBGRID;

/** 数据维护关联表 **/
drop table if exists T_DBGRID_RELATION;
create table T_DBGRID_RELATION (
	ID bigint comment '主键',
    STRUCT_ID bigint NOT NULL comment '配置表记录ID',
    TABLE_NAME varchar(128) NOT NULL comment '表名',
    ALIAS varchar(128) comment '别名',
    FIELD varchar(128) NOT NULL comment '字段名',
    BIND_TABLE_NAME varchar(128) NOT NULL comment '关联表名',
    BIND_FIELD varchar(128) NOT NULL comment '关联字段名',
    primary key(ID)
) comment '数据维护关联表';
create index IDX_STRUCTID on T_DBGRID_RELATION(STRUCT_ID);
select * from T_DBGRID_RELATION;


/** 数据维护编辑字段表 **/
drop table if exists T_DBGRID_FIELD;
create table T_DBGRID_FIELD (
	ID bigint comment '主键',
    STRUCT_ID bigint NOT NULL comment '配置表记录ID',
    NAME varchar(128) NOT NULL comment '名称',
    TABLE_NAME varchar(128) NOT NULL comment '表名',
    FIELD varchar(128) NOT NULL comment '字段名',
    FIELD_TYPE int default 0 comment '字段类型',
    PK int default 0 comment '主键类型（0=非主键，1=输入主键，2=GUID主键，3=伪ID主键）',
    WIDTH int default 0 comment '宽度',
    ALIGN varchar(32) comment '别名',
    EDIT_SCOPE int default 0 comment '编辑范围（0=始终可编辑，1=仅新增时可编辑，2=不可编辑）',
    EDIT_TYPE int default 0 comment '编辑类型',
    EDIT_OPTIONS text comment '编辑选项',
    PLACEHOLDER varchar(128) comment '占位提示',
    DEFAULT_VALUE varchar(128) comment '默认值',
    NULLABLE tinyint(1) default 1 comment '是否可为空',
    HIDDEN tinyint(1) default 0 comment '是否隐藏', 
    ORDER_INDEX int default 0 comment '排序值',
    primary key(ID)
) comment '数据维护编辑字段表';
create index IDX_STRUCTID on T_DBGRID_FIELD(STRUCT_ID);
select * from T_DBGRID_FIELD  order by ORDER_INDEX;


/** 数据排序表 **/
drop table if exists T_DBGRID_FIELD_ORDERBY;
create table T_DBGRID_FIELD_ORDERBY (
	ID bigint comment '主键',
    STRUCT_ID bigint NOT NULL comment '配置表记录ID',
    TABLE_NAME varchar(128) NOT NULL comment '表名',
    FIELD varchar(128) NOT NULL comment '字段名',
    DIRECT tinyint(1) default 0 comment '排序方向（0=正序，1=反序）',
    ORDER_INDEX int default 0 comment '排序值',
    primary key(ID)
) comment '数据排序表';
create index IDX_STRUCTID on T_DBGRID_FIELD_ORDERBY(STRUCT_ID);
select * from T_DBGRID_FIELD_ORDERBY  order by ORDER_INDEX asc;

/** SQL原型️表 **/
drop table if exists T_SQLS;
create table T_SQLS (
	ID bigint comment '主键',
    CONTENT longtext comment 'SQL正文',
    primary key(ID)
) comment 'SQL原型️表';
select * from T_SQLS;


-- ---已更新-----


INSERT INTO `zinger`.`role_menu` (`ROLE_ID`, `MENU_ID`, `SERVER_ID`, `CREATE_BY`, `CREATE_TIME`, `UPDATE_BY`, `UPDATE_TIME`) VALUES ('159597393644355584', 'Workflow', 'uc', '161898830143422464', '2021-05-25 20:20:00', NULL, NULL);
INSERT INTO `zinger`.`role_menu` (`ROLE_ID`, `MENU_ID`, `SERVER_ID`, `CREATE_BY`, `CREATE_TIME`, `UPDATE_BY`, `UPDATE_TIME`) VALUES ('159597393644355584', 'WorkflowTemplate', 'uc', '161898830143422464', '2021-05-25 20:20:00', NULL, NULL);
INSERT INTO `zinger`.`role_menu` (`ROLE_ID`, `MENU_ID`, `SERVER_ID`, `CREATE_BY`, `CREATE_TIME`, `UPDATE_BY`, `UPDATE_TIME`) VALUES ('159597393644355584', 'CustomWorkflow', 'uc', '161898830143422464', '2021-05-25 20:20:00', NULL, NULL);
INSERT INTO `zinger`.`role_menu` (`ROLE_ID`, `MENU_ID`, `SERVER_ID`, `CREATE_BY`, `CREATE_TIME`, `UPDATE_BY`, `UPDATE_TIME`) VALUES ('159597393644355584', 'WorkflowPermission', 'uc', '161898830143422464', '2021-05-25 20:20:00', NULL, NULL);
















