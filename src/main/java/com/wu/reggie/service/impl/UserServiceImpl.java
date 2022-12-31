package com.wu.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wu.reggie.entity.User;
import com.wu.reggie.mapper.UserMapper;
import com.wu.reggie.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Resource
    private JavaMailSender mailSender;//邮箱服务

    @Value("${spring.mail.username}")//配置文件中获取的信息
    private String from;//邮件发送人


    @Override
    public void sendMsg(String to, String subject, String context) {
        SimpleMailMessage msg = new SimpleMailMessage();

        msg.setFrom(from);
        msg.setTo(to);
        msg.setSubject(subject);
        msg.setText(context);

        mailSender.send(msg);
    }

}
