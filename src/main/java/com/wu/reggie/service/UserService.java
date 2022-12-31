package com.wu.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wu.reggie.entity.User;


public interface UserService extends IService<User> {

    /**
     * 使用QQ邮箱发送验证码
     * @param to
     * @param subject
     * @param context
     */
    void sendMsg(String to,String subject, String context);
}
