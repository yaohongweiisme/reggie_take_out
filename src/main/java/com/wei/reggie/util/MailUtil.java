package com.wei.reggie.util;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class MailUtil implements Runnable {
    private String email;// 收件人邮箱
    private String code;// 激活码
 
    public MailUtil(String email, String code) {
        this.email = email;
        this.code = code;
    }
 
    public void run() {
        // 1.创建连接对象javax.mail.Session
        // 2.创建邮件对象 javax.mail.Message
        // 3.发送一封激活邮件
        String from = "qq2558939179@163.com";// 发件人电子邮箱
        String host = "smtp.163.com"; // 指定发送邮件的主机smtp.qq.com(QQ)|smtp.163.com(网易)
 
        Properties properties = System.getProperties();// 获取系统属性
 
        properties.setProperty("mail.smtp.host", host);// 设置邮件服务器
        properties.setProperty("mail.smtp.auth", "true");// 打开认证
 
        try {

 
 
            // 1.获取默认session对象
            Session session = Session.getDefaultInstance(properties, new Authenticator() {
                public PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication("qq2558939179@163.com", "BALOYZFGSUOBLWAP"); // 发件人邮箱账号、授权码
                }
            });
 
            // 2.创建邮件对象
            Message message = new MimeMessage(session);
            // 2.1设置发件人
            message.setFrom(new InternetAddress(from));
            // 2.2设置接收人
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(email));
            // 2.3设置邮件主题
            message.setSubject("登陆验证码");
            // 2.4设置邮件内容
            String content = "<html><head></head><body><h1>这是一封登录验证码激活邮件,登录请输入以下验证码</h1><h3>你的验证码是:" + code
                    + "请妥善保管</h3></body></html>";
            message.setContent(content, "text/html;charset=UTF-8");
            // 3.发送邮件
            Transport.send(message);
            System.out.println("邮件成功发送!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}