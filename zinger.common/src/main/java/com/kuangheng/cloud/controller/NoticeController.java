package com.kuangheng.cloud.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kuangheng.cloud.dao.CatalogDao;
import com.kuangheng.cloud.dao.NoticeDao;
import com.kuangheng.cloud.dao.NoticeReadDao;
import com.kuangheng.cloud.dao.NoticeReplyDao;
import com.kuangheng.cloud.entity.Notice;
import com.kuangheng.cloud.entity.NoticeRead;
import com.kuangheng.cloud.entity.NoticeReply;
import com.wisdge.cloud.dto.ApiResult;
import com.wisdge.cloud.dto.LoginUser;
import com.wisdge.cloud.internal.CoreConstant;
import com.wisdge.cloud.util.Payload;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/common/notice")
public class NoticeController extends  BaseController {
    private static final String MSG_REINDEX_KEY = "msg_reindex";


    @Resource
    private NoticeDao noticeDao;

    @Resource
    private NoticeReplyDao noticeReplyDao;

    @Resource
    private NoticeReadDao noticeReadDao;

    @Resource
    private CatalogDao catalogDao;

    @PostMapping("/touch")
    @ApiOperation("获取未读公告")
    public ApiResult touchNotice() throws IOException {
        Payload payload = new Payload(request);
        String timestamp = payload.get("timestamp");
        if (timestamp == null)
            return ApiResult.fail("Timestamp not defined");

        log.debug("Touch notice: {}", timestamp);
        LoginUser loginUser = getLoginUser();
        return ApiResult.ok("", noticeDao.touch(timestamp, loginUser.getId(), loginUser.getRoles()));
    }

    @GetMapping("/catalogues")
    @ApiOperation("获取公告类型")
    public ApiResult getCatalogues() {
        LoginUser loginUser = getLoginUser();
        return ApiResult.ok("", catalogDao.selectList(null));
    }

    @PostMapping("/list")
    @ApiOperation("获取公告列表")
    public ApiResult listNotices() throws IOException {
        Payload payload = new Payload(request);
        String scope = payload.getString("scope", "all");
        int pageIndex = payload.getInt(CoreConstant.PAGE_IDNEX, 1);
        int pageSize = payload.getInt(CoreConstant.PAGE_SIZE, 50);
        LoginUser loginUser = getLoginUser();
        IPage<Notice> page = new Page<>(pageIndex, pageSize);
        if ("read".equalsIgnoreCase(scope))
            return ApiResult.ok("", noticeDao.listRead(page, loginUser.getId(), loginUser.getRoles()));
        else if ("unread".equalsIgnoreCase(scope))
            return ApiResult.ok("", noticeDao.listUnread(page, loginUser.getId(), loginUser.getRoles()));
        else
            return ApiResult.ok("", noticeDao.list(page, loginUser.getId(), loginUser.getRoles()));
    }

    @PostMapping("/get")
    @ApiOperation("获取一条公告")
    public ApiResult getNoticeById() throws IOException {
        Payload payload = new Payload(request);
        String id = payload.get("id");

        LoginUser loginUser = getLoginUser();
        Notice notice = noticeDao.getById(id, loginUser.getId(), loginUser.getRoles());
        return ApiResult.ok("", notice);
    }

    @PostMapping("/read")
    @ApiOperation("获取一条公告")
    public ApiResult commitNoticeRead() throws IOException {
        Payload payload = new Payload(request);
        String id = payload.get("id");

        LoginUser loginUser = getLoginUser();
        NoticeRead noticeRead = new NoticeRead();
        noticeRead.setId(newId());
        noticeRead.setNoticeId(id);
        noticeRead.setUserId(loginUser.getId());
        noticeRead.setReadTime(new Date());
        if (noticeReadDao.insert(noticeRead) > 0)
            return ApiResult.ok();

        return ApiResult.fail("数据库访问失败");
    }

    @PostMapping("/reply")
    @ApiOperation("回复公告")
    public ApiResult replyNotice() throws IOException {
        Payload payload = new Payload(request);
        String id = payload.get("id");
        String content = payload.get("content");

        LoginUser loginUser = getLoginUser();
        NoticeReply reply = new NoticeReply();
        reply.setId(newId());
        reply.setNoticeId(id);
        reply.setContent(content);
        reply.setCreateBy(loginUser.getId());
        reply.setCreateTime(new Date());
        if (noticeReplyDao.insert(reply) > 0)
            return ApiResult.ok();
        else
            return ApiResult.fail("数据库访问失败");
    }

    @PostMapping("/getReplies")
    @ApiOperation("获取一条公告")
    public ApiResult getNoticeReplies() throws IOException {
        Payload payload = new Payload(request);
        String id = payload.get("id");
        return ApiResult.ok("", noticeReplyDao.findAll(id));
    }

    @PostMapping("/getByMe")
    @ApiOperation("获取一条我发送的公告")
    public ApiResult getNoticeByMe() throws IOException {
        Payload payload = new Payload(request);
        String id = payload.get("id");

        LoginUser loginUser = getLoginUser();
        Notice notice = noticeDao.getNoticeByMe(id, loginUser.getId());
        return ApiResult.ok("", notice);
    }

    @PostMapping("/attachments")
    @ApiOperation("获取公告的附件")
    public ApiResult getNoticeAttachments() throws IOException {
        Payload payload = new Payload(request);
        String id = payload.get("id");
        return ApiResult.ok("", noticeDao.findAttachments(id));
    }

    @GetMapping("/getCount")
    @ApiOperation("获取公告列表数量")
    public ApiResult getCount() {
        LoginUser loginUser = getLoginUser();
        IPage<Notice> page = new Page<>(1, 50);
        Page<Notice> noticePage = noticeDao.list(page, loginUser.getId(), loginUser.getRoles());
        long total = noticePage.getTotal();
        Page<Notice> readNoticePage = noticeDao.listRead(page, loginUser.getId(), loginUser.getRoles());
        long readTotal = readNoticePage.getTotal();
        Page<Notice> unreadNoticePage = noticeDao.listUnread(page, loginUser.getId(), loginUser.getRoles());
        long unreadTotal = unreadNoticePage.getTotal();
        Map result = new HashMap();
        result.put("total", total);
        result.put("readTotal", readTotal);
        result.put("unreadTotal", unreadTotal);
        return ApiResult.ok("", result);
    }

}
