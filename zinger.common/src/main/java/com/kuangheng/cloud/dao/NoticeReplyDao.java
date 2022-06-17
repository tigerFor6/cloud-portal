package com.kuangheng.cloud.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kuangheng.cloud.entity.NoticeReply;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface NoticeReplyDao extends BaseMapper<NoticeReply> {
    @Select("select " +
            "    R.*, U.NAME as CREATE_BY_NAME, U.AVATAR " +
            "    from MSG_NOTICE_REPLY R " +
            "    left join SYS_USER U on U.ID=R.CREATE_BY " +
            "where " +
            "    R.NOTICE_ID=#{noticeId} " +
            "    order by R.CREATE_TIME desc")
    List<NoticeReply> findAll(@Param("noticeId") String noticeId);
}
