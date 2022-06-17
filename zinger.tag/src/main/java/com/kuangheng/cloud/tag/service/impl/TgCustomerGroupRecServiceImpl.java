package com.kuangheng.cloud.tag.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kuangheng.cloud.common.constant.OperationType;
import com.kuangheng.cloud.dao.UserDao;
import com.kuangheng.cloud.entity.User;
import com.kuangheng.cloud.service.BaseService;
import com.kuangheng.cloud.tag.entity.TgCustomerGroupEntity;
import com.wisdge.utils.SnowflakeIdWorker;
import com.wisdge.utils.StringUtils;
import jodd.util.StringUtil;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kuangheng.cloud.tag.dto.TgCustomerGroupRecDTO;
import com.kuangheng.cloud.tag.dao.TgCustomerGroupRecDao;
import com.kuangheng.cloud.tag.entity.TgCustomerGroupRecEntity;
import com.kuangheng.cloud.tag.service.TgCustomerGroupRecService;

import java.util.List;


@Service("tgCustomerGroupRecService")
public class TgCustomerGroupRecServiceImpl extends BaseService<TgCustomerGroupRecDao, TgCustomerGroupRecEntity> implements TgCustomerGroupRecService {

    @Autowired
    private TgCustomerGroupRecDao tgCustomerGroupRecDao;

    @Autowired
    private SnowflakeIdWorker snowflakeIdWorker;

    @Autowired
    private UserDao userDao;

    @Override
    public IPage queryPage(TgCustomerGroupRecDTO tgCustomerGroupRecDTO) {
        IPage<TgCustomerGroupRecEntity> queryPage = new Page<>(tgCustomerGroupRecDTO.getPage(), tgCustomerGroupRecDTO.getSize());
        QueryWrapper queryWrapper = new QueryWrapper<TgCustomerGroupRecEntity>(tgCustomerGroupRecDTO);
        queryWrapper.orderByDesc("VERSION");
        queryWrapper.eq("CUSTOMER_GROUP_ID", tgCustomerGroupRecDTO.getCustomerGroupId());

        IPage<TgCustomerGroupRecEntity> iPage = this.page(queryPage, queryWrapper);
        if (CollectionUtils.isNotEmpty(iPage.getRecords())) {
            for (TgCustomerGroupRecEntity tgCustomerGroupRecEntity : iPage.getRecords()) {
                User user = userDao.selectById(tgCustomerGroupRecEntity.getCreateBy());
                if (user != null) {
                    tgCustomerGroupRecEntity.setCreateByName(user.getName());
                    tgCustomerGroupRecEntity.setUpdateByName(user.getName());
                }
            }
        }
        return iPage;
    }

    @Override
    public int saveCustomerGroupRec(TgCustomerGroupEntity tgCustomerGroupEntity) {
        //查询当前的版本号
        OperationType type = OperationType.INSERT;
        if (StringUtils.isNotBlank(tgCustomerGroupEntity.getId())) {
            type = OperationType.UPDATE;
        }
        Integer version = null;
        if (type != OperationType.INSERT) {
            version = tgCustomerGroupRecDao.getVersion(tgCustomerGroupEntity.getId());
        }
        if (version == null) {
            version = 0;
        }
        version++;
        long snowId = snowflakeIdWorker.nextId();
        return tgCustomerGroupRecDao.save(tgCustomerGroupEntity.getId(), version, snowId, type.getCode());
    }

}
