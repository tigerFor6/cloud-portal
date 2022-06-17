package com.kuangheng.cloud.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.kuangheng.cloud.constant.Constant;
import com.kuangheng.cloud.dao.ResDao;
import com.kuangheng.cloud.dao.ResModuleDao;
import com.kuangheng.cloud.entity.Res;
import com.kuangheng.cloud.entity.ResModule;
import com.kuangheng.cloud.entity.ResRole;
import com.wisdge.cloud.dto.ApiResult;
import com.wisdge.cloud.dto.LoginUser;
import com.wisdge.cloud.util.Payload;
import com.wisdge.utils.StringUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/common/res/api")
@Api(tags="ResController")
public class APiController extends BaseController {

    @Resource
    private ResDao resDao;

    @Resource
    private ResModuleDao resModuleDao;

    private static final String ZINGER_MODULE_ID = "193823858548015104";

    @GetMapping("/find")
    @ApiOperation(value = "获取知决模块api服务资源")
    public ApiResult<List<Res>> findRes() {
        return findApi(Constant.RESOURCE_API);
    }


    private ApiResult<List<Res>> findApi(String type) {
        QueryWrapper<Res> queryWrapper = new QueryWrapper();
        queryWrapper.eq("RES_TYPE", type).eq("RES_MODULE_ID", ZINGER_MODULE_ID).orderByAsc("RES_NAME");
        return ApiResult.ok("", resDao.selectList(queryWrapper));
    }

    @PostMapping("/findByRoleId")
    @ApiOperation(value = "获取角色下的api资源")
    public ApiResult<List<Res>> findResByRoleId() throws IOException {
        Payload payload = new Payload(request);
        String roleId = payload.getString("roleId", "");
        List<String> resIds = resDao.findResByRoleId(roleId);
        List<Res> res = new ArrayList<>();
        if (resIds.size() > 0 ){
            res = resDao.selectBatchIds(resIds);
            res = res.stream().filter(e -> ZINGER_MODULE_ID.equals(e.getModuleId()) && Constant.RESOURCE_API.equals(e.getType())).collect(Collectors.toList());
        }
        return ApiResult.ok("资源列表", res);
    }

    @Transactional
    @PostMapping("/set")
    @ApiOperation(value = "设置服务资源权限")
    public ApiResult set() throws IOException {
        Payload payload = new Payload(request);
        String resIds = payload.getString("resIds", "");
        String roleId = payload.getString("roleId", "");
        LoginUser user = getLoginUser();
        Date now = new Date();
        List<ResRole> resRoles = new ArrayList<>();
        for(String id:resIds.split(",")) {
            resRoles.add(new ResRole(newId(), id, roleId, user.getId(), now));
        }
        QueryWrapper<Res> queryWrapper = new QueryWrapper();
        queryWrapper.eq("RES_TYPE", Constant.RESOURCE_API).eq("RES_MODULE_ID", ZINGER_MODULE_ID);
        List<Res> res = resDao.selectList(queryWrapper);
        resDao.deleteBatch(roleId, res);
        int insert = (StringUtils.isEmpty(resIds)? 0 : resDao.addRoles(roleId, resRoles));
        return ApiResult.ok("", insert);
    }

    @PostMapping("/remove")
    @ApiOperation(value = "删除服务资源权限")
    public ApiResult removeApi() throws IOException {
        Payload payload = new Payload(request);
        String resId = payload.getString("resId", "");
        String roleId = payload.getString("roleId", "");
        if (resDao.removeRole(resId, roleId) > 0) {
            return ApiResult.ok();
        } else {
            return ApiResult.fail("数据库无此条记录");
        }
    }
}
