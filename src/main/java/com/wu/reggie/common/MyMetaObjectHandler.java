package com.wu.reggie.common;

import ch.qos.logback.classic.Logger;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 自定义元数据处理器
 */

@Component
@Slf4j
public class MyMetaObjectHandler implements MetaObjectHandler {

    /**
     * 对插入操作进行自动填充
     * @param metaObject
     */

    @Override
    public void insertFill(MetaObject metaObject) {
        log.info("插入数据时....");

        metaObject.setValue("createTime", LocalDateTime.now());
        metaObject.setValue("updateTime",LocalDateTime.now());

        metaObject.setValue("createUser",BaseContext.getCurrentId());
        metaObject.setValue("updateUser",BaseContext.getCurrentId());


    }

    @Override
    /**
     * 对更新操作进行自动填充
     */
    public void updateFill(MetaObject metaObject) {
        log.info("数据更新时....");
        log.info(metaObject.toString());
//        long id = Thread.currentThread().getId();
//        log.info("线程是：{}",id);
        metaObject.setValue("updateTime",LocalDateTime.now());
        metaObject.setValue("updateUser",BaseContext.getCurrentId());
    }
}
