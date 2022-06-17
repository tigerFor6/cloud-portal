package com.kuangheng.cloud.tag.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.kuangheng.cloud.controller.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.wisdge.cloud.dto.ApiResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import com.kuangheng.cloud.tag.entity.TgCustomerGroupRecEntity;
import com.kuangheng.cloud.tag.service.TgCustomerGroupRecService;
import com.kuangheng.cloud.tag.dto.TgCustomerGroupRecDTO;

/**
 * 客户群组修改记录表
 *
 * @author NLP
 * @email ninglp@kuangheng.com
 * @date 2021-06-02 11:30:11
 */
@Api(tags = "客户群组修改记录")
@RestController
@RequestMapping("/tag/customergrouprec")
public class TgCustomerGroupRecController extends BaseController {
    @Autowired
    private TgCustomerGroupRecService tgCustomerGroupRecService;

    /**
     * 列表
     */
    @PostMapping("/list")
    @ApiOperation(value = "客户群组修改记录表列表", notes = "客户群组修改记录表列表")
    public ApiResult<IPage<TgCustomerGroupRecEntity>> list(@RequestBody TgCustomerGroupRecDTO tgCustomerGroupRecDTO) {
        String customerGroupId = tgCustomerGroupRecDTO.getCustomerGroupId();
        if (tgCustomerGroupRecDTO.getPage() == null) {
            tgCustomerGroupRecDTO.setPage(1);
        }
        if (tgCustomerGroupRecDTO.getSize() == null) {
            tgCustomerGroupRecDTO.setSize(100);
        }
        tgCustomerGroupRecDTO.setCustomerGroupId(customerGroupId);
        IPage page = tgCustomerGroupRecService.queryPage(tgCustomerGroupRecDTO);

        return ApiResult.ok("page", page);
    }


    /**
     * 信息
     */
//    @GetMapping("/info/{id}")
//    @ApiOperation(value = "客户群组修改记录表信息", notes = "客户群组修改记录表信息")
//    public ApiResult<TgCustomerGroupRecEntity> info(@PathVariable("id") Long id) {
//        TgCustomerGroupRecEntity tgCustomerGroupRec = tgCustomerGroupRecService.getById(id);
//
//        return ApiResult.ok("tgCustomerGroupRec", tgCustomerGroupRec);
//    }

}
