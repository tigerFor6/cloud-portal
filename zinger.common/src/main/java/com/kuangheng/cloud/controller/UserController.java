package com.kuangheng.cloud.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kuangheng.cloud.dao.UserDao;
import com.kuangheng.cloud.entity.User;
import com.wisdge.cloud.dto.ApiResult;
import com.wisdge.cloud.internal.CoreConstant;
import com.wisdge.cloud.util.Payload;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/user")
@Api(tags="UserController")
public class UserController extends BaseController {
    @Autowired
    private UserDao userDao;

    @PostMapping("/findAll")
    @ApiOperation(value = "获取知决系统用户记录")
    public ApiResult findAll() throws IOException {
        Payload payload = new Payload(request);
        int pageIndex = payload.getInt(CoreConstant.PAGE_IDNEX, 1);
        int pageSize = payload.getInt(CoreConstant.PAGE_SIZE, config.getDefaultPageSize());
        Page<User> queryPage = new Page<>(pageIndex, pageSize);
        QueryWrapper<User> queryWrapper = new QueryWrapper();
        queryWrapper.eq("STATUS", 1).eq("MODULE_ID", "193823858548015104").orderByAsc("FULLNAME");
        return ApiResult.ok("", userDao.selectPage(queryPage, queryWrapper));
    }

    @PostMapping("/search")
    @ApiOperation(value = "查询用户记录")
    public ApiResult search() throws IOException {
        Payload payload = new Payload(request);
        String key = payload.getString("key", "");
        int pageIndex = payload.getInt(CoreConstant.PAGE_IDNEX, 1);
        int pageSize = payload.getInt(CoreConstant.PAGE_SIZE, config.getDefaultPageSize());
        Page<User> queryPage = new Page<>(pageIndex, pageSize);
        QueryWrapper<User> queryWrapper = new QueryWrapper();
        queryWrapper
                .like("U.NAME", key)
                .or()
                .like("U.PHONE", key)
                .or()
                .like("U.EMP_ID", key)
                .orderByAsc("U.FULLNAME");
        return ApiResult.ok("", userDao.search(queryPage, queryWrapper));
    }

    @PostMapping("/roles")
    @ApiOperation(value = "获取用户的所有角色记录")
    public ApiResult findRoles() throws IOException {
        Payload payload = new Payload(request);
        String userId = payload.getString("id", "");
        return ApiResult.ok("", userDao.findRoles(userId));
    }

    @PostMapping("/avatar")
    @ApiOperation(value = "查询用户头像")
    public ApiResult userAvatar() throws IOException {
        Payload payload = new Payload(request);
        String userId = payload.get("id");
        return ApiResult.ok("", userDao.getAvatar(userId));
    }
}
