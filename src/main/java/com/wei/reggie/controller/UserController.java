package com.wei.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wei.reggie.common.R;
import com.wei.reggie.entity.User;
import com.wei.reggie.service.UserService;
import com.wei.reggie.util.MailUtil;
import com.wei.reggie.util.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession session){
        String email=user.getEmail();
        if(StringUtils.hasText(email)){
            String code= ValidateCodeUtils.generateValidateCode(6).toString();
            log.info("验证码为:{}",code);
            session.setAttribute("emailCode",code);
            new MailUtil(user.getEmail(),code).run();
            return R.success("发送验证码成功");
        }
        return R.error("发送验证码失败");
    }

    @PostMapping("/login")
    public R<User> login(@RequestBody Map map, HttpSession session){
        String email=map.get("email").toString();
        String code=map.get("code").toString();
        Object codeInSession=session.getAttribute("emailCode");
        User user;
        if(codeInSession!=null&&codeInSession.equals(code)){
            LambdaQueryWrapper<User> queryWrapper=new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getEmail,email);
            user = userService.getOne(queryWrapper);
            if(user==null){
                user=new User();
                user.setEmail(email);
                userService.save(user);
            }
            session.setAttribute("user",user.getId());
            return R.success(user);
        }
        return R.error("登录失败");

    }

}
