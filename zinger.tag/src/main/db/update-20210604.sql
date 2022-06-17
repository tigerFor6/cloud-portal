ALTER TABLE `met_event_field`
    ADD COLUMN `SORT`  int(11) NULL DEFAULT NULL COMMENT '排序' AFTER `STATUS`;

ALTER TABLE `met_event_field`
    ADD COLUMN `EVENT_ID`  bigint(20) NULL DEFAULT NULL COMMENT '事件id' AFTER `ID`;

-- ----已更新