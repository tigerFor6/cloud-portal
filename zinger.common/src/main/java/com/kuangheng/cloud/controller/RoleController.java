package com.kuangheng.cloud.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.kuangheng.cloud.dao.RoleDao;
import com.kuangheng.cloud.entity.Role;
import com.wisdge.cloud.dto.ApiResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@Slf4j
@RestController
@RequestMapping("/common/role")
@Api(tags="RoleController")
public class RoleController extends BaseController {

    @Resource
    private RoleDao roleDao;

    @GetMapping("/findAll")
    @ApiOperation(value = "获取全部角色记录（不包含非正常记录）")
    public ApiResult findAll() {
        QueryWrapper<Role> queryWrapper = new QueryWrapper();
        queryWrapper.eq("status", 1).orderByAsc("name");
        return ApiResult.ok("", roleDao.selectList(queryWrapper));
    }
}
