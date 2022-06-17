package com.kuangheng.cloud.controller;

import com.kuangheng.cloud.common.exception.BusinessException;
import com.kuangheng.cloud.constant.I18nConstantCode;
import com.kuangheng.cloud.dao.LockDao;
import com.kuangheng.cloud.entity.Locker;
import com.wisdge.cloud.dto.ApiResult;
import com.wisdge.cloud.dto.LoginUser;
import com.wisdge.cloud.util.Payload;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Date;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/common/lock")
@Api(tags = "LockController")
public class LockController extends BaseController {

    @Resource
    private LockDao lockMapper;

    @PostMapping("/check")
    @ApiOperation("查询业务锁状态")
    public ApiResult lockCheck() throws IOException {
        Payload payload = new Payload(request);
        String service = payload.get("service");
        String mainKey = payload.get("mainKey");
        String subKey = payload.get("subKey");

        LoginUser loginUser = getLoginUser();
        List<Locker> lockers = lockMapper.check(service, mainKey, subKey);
        if (lockers.size() > 0) {
            Locker locker = lockers.get(0);
            if (locker.getCreateBy().equals(loginUser.getId()))
                return ApiResult.ok("", locker);
            else
                return ApiResult.lockByOther(locker);
        } else
            return ApiResult.unlock();
    }

    @PostMapping("/do")
    @ApiOperation("插入业务锁")
    public ApiResult lock() throws IOException {
        Payload payload = new Payload(request);
        String service = payload.get("service");
        String mainKey = payload.get("mainKey");
        String subKey = payload.get("subKey");

        LoginUser loginUser = getLoginUser();
        List<Locker> lockers = lockMapper.check(service, mainKey, subKey);
        if (lockers.size() > 0) {
            Locker locker = lockers.get(0);
            if (locker.getCreateBy().equals(loginUser.getId()))
                return ApiResult.ok("Locked already", locker);
            else
                return ApiResult.lockByOther(locker);
        } else {
            Locker locker = new Locker();
            locker.setId(newId());
            locker.setService(service);
            locker.setMainKey(mainKey);
            locker.setSubKey(subKey);
            locker.setCreateBy(loginUser.getId());
            locker.setUserName(loginUser.getName());
            locker.setCreateTime(new Date());
            if (lockMapper.insert(locker) > 0) {
                return ApiResult.ok("", locker);
            } else {
                throw new BusinessException(I18nConstantCode.DB_ERROR);
            }
        }
    }


    @PostMapping("/recall")
    @ApiOperation("销毁业务锁")
    public ApiResult recall() throws IOException {
        Payload payload = new Payload(request);
        String service = payload.get("service");
        String mainKey = payload.get("mainKey");
        String subKey = payload.get("subKey");

        List<Locker> lockers = lockMapper.check(service, mainKey, subKey);
        if (lockers.size() > 0) {
            Locker locker = lockers.get(0);
            if (!locker.getCreateBy().equals(getLoginUser().getId())) {
                return ApiResult.lockByOther(locker);
            }

            if (lockMapper.deleteById(lockers.get(0).getId()) > 0)
                return ApiResult.ok();
            else
                throw new BusinessException(I18nConstantCode.DB_ERROR);
        } else {
            return ApiResult.ok();
        }
    }
}
