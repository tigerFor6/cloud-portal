package com.kuangheng.cloud.service;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wisdge.cloud.dto.LoginUser;
import com.wisdge.utils.SnowflakeIdWorker;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.Field;
import java.util.Date;

public class BaseService<M extends BaseMapper<T>, T> extends ServiceImpl<M, T> {

    @Autowired
    protected SnowflakeIdWorker snowflakeIdWorker;

    protected String newId() {
        return String.valueOf(snowflakeIdWorker.nextId());
    }

    /**
     * 保存或者根据id更新
     *
     * @param entity
     * @return
     */
    public boolean saveOrUpdate(T entity, LoginUser user) {
        String userId = user.getId();
        Date date = new Date();
        //使用反射设置更新字段
        try {
            Field updaterField = entity.getClass().getDeclaredField("updateBy");
            updaterField.setAccessible(true);
            updaterField.set(entity, userId);
        } catch (NoSuchFieldException | IllegalAccessException e) {
        }
        try {
            Field updateDateField = entity.getClass().getDeclaredField("updateTime");
            updateDateField.setAccessible(true);
            updateDateField.set(entity, date);
        } catch (NoSuchFieldException | IllegalAccessException e) {
        }
        Object id = null;
        try {
            Field idField = entity.getClass().getDeclaredField("id");
            idField.setAccessible(true);
            id = idField.get(entity);
        } catch (NoSuchFieldException | IllegalAccessException e) {
        }
        //如果id为空的话，则认为是新增，否则为数据更新
        if (id == null || StringUtils.isBlank(id.toString())) {
            try {
                Field creatorField = entity.getClass().getDeclaredField("createBy");
                creatorField.setAccessible(true);
                creatorField.set(entity, userId);
            } catch (NoSuchFieldException | IllegalAccessException e) {
            }
            try {
                Field createDateField = entity.getClass().getDeclaredField("createTime");
                createDateField.setAccessible(true);
                createDateField.set(entity, date);
            } catch (NoSuchFieldException | IllegalAccessException e) {
            }
            return this.save(entity);
        } else {
            return this.updateById(entity);
        }
    }
}
