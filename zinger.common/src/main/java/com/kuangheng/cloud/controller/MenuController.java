package com.kuangheng.cloud.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.kuangheng.cloud.dao.MenuDao;
import com.kuangheng.cloud.entity.Menu;
import com.wisdge.cloud.dto.ApiResult;
import com.wisdge.utils.StringUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/common/menu")
@Api(tags="MenuController")
public class MenuController extends BaseController {

    @Resource
    private MenuDao menuDao;

    @GetMapping("/getByRoles")
    @ApiOperation(value = "获取当前用户所有角色的前台菜单配置")
    public ApiResult getByRoles() {
        List<String> menuIds = menuDao.selectMenuIdsByRoles(getLoginUser().getRoles());
        List<String> menus = new ArrayList<>();
        for(String menuId:menuIds) {
            if (! menus.contains(menuId))
                menus.add(menuId);
        }
        List<Menu> menuList = new ArrayList<Menu>();
        if (menus.size() > 0 ){
            menuList = menuDao.selectBatchIds(menus);
            if (menuList.size() > 0){
                menuList = menuList.stream().filter(m -> StringUtils.isNotBlank(m.getParentId())).collect(Collectors.toList());
                Map<String, List<Menu>> group = menuList.stream().collect(Collectors.groupingBy(Menu::getParentId));
                group.forEach((k,v)->{
                    menus.add(k);
                });
                menuList = menuDao.selectBatchIds(menus);
                menuList = menuList.stream().sorted(Comparator.comparing(Menu::getOrderIndex)).collect(Collectors.toList());
            }
        }
        return ApiResult.ok("菜单列表", menuList);
    }
}
