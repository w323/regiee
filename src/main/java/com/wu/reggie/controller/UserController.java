package com.wu.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wu.reggie.common.R;
import com.wu.reggie.entity.User;
import com.wu.reggie.service.UserService;
import com.wu.reggie.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Map;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 发送手机短信验证码(邮箱验证)
     *
     * @param user
     * @return
     */
    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession session) {
        //获取手机号
        String phone = user.getPhone();

        String subject = "瑞吉外卖登录验证码";
        if (StringUtils.isNotEmpty(phone)) {
            //生成随机的4位验证码
            String code = ValidateCodeUtils.generateValidateCode(4).toString();
            String context = "欢迎使用瑞吉外卖，登录验证码是：" + code + ",五分钟内有效，请勿泄露";
            log.info("code={}", code);

            //调用QQ邮箱服务
            //userService.sendMsg(phone, subject, context);

            /*
            使用伪验证，暂时先不发送真的验证码
             */

            //需要将生成的验证码保存到Session
            session.setAttribute(phone, code);

            return R.success("验证码发送成功");
        }

        return R.error("验证码发送失败");
    }

    /**
     * 移动端用户登录
     *
     * @param map
     * @param session
     * @return
     */
    @PostMapping("/login")
    public R<User> login(@RequestBody Map map, HttpSession session) {
        log.info(map.toString());

        //获取账号
        String phone = (String) map.get("phone");
        //获取验证码
        String code = (String) map.get("code");

        //从session获取验证码
        String codeInSession = (String) session.getAttribute(phone);

        //比较
        if(codeInSession != null && code.equals(codeInSession)){
            //如果比对成功，说明登陆成功
            //如果账号没存在数据库中，就直接对其进行注册
            LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
            userLambdaQueryWrapper.eq(User::getPhone,phone);
            User user = userService.getOne(userLambdaQueryWrapper);

            if(user == null) {
                user = new User();
                user.setPhone(phone);
                user.setStatus(1);
                userService.save(user);//保存到数据库
            }
            session.setAttribute("user",user.getId());
            return R.success(user);
        }
        //如果账号没存在数据库中，就直接对其进行注册
        return R.error("登录失败");
    }

}
