package com.kuangheng.cloud.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kuangheng.cloud.entity.Notice;
import com.kuangheng.cloud.entity.NoticeAttach;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface NoticeDao extends BaseMapper<Notice> {

    List<Notice> touch(@Param("timestamp") String timestamp,
                       @Param("userId") String userId,
                       @Param("roles") List<String> roles);

    Page<Notice> list(IPage<Notice> page, @Param("userId") String userId, @Param("roles") List<String> roles);

    Page<Notice> listRead(IPage<Notice> page, @Param("userId") String userId, @Param("roles") List<String> roles);

    Page<Notice> listUnread(IPage<Notice> page, @Param("userId") String userId, @Param("roles") List<String> roles);

    Notice getById(@Param("id") String id, @Param("userId") String userId, @Param("roles") List<String> roles);

    Notice getNoticeByMe(@Param("noticeId") String noticeId, @Param("userId") String userId);

    @Select("select * from MSG_NOTICE_ATTACH where NOTICE_ID=#{noticeId}")
    List<NoticeAttach> findAttachments(@Param("noticeId") String noticeId);
}
