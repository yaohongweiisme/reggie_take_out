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
            message.setSubject("致黄老师的一封信");
            // 2.4设置邮件内容
            String content = "<html><head></head><body>"+
                    "親愛的黃老師：\n" +
                    "\n" +
                    "　　您好，在這個學期，我們認按五年識的時間不是很長，但是我已經感受到您對我們的愛。我們班是全五年級最難教的，但是您把我們治的服服帖帖的，我們真的很感謝您，我們永遠的黃老師。\n" +
                    "\n" +
                    "　　您為了我們，不知道喊破了多少次喉嚨，但是我們也曾為感覺到。您為了我們，不知道用了多少只紅筆，辛勤的為我們批改作業。您為了我們，不知道有多少次留在辦公室里預習課文，準備把新的知識交給我們。\n" +
                    "\n" +
                    "　　有一次教師節，黃老師作出了一個驚人的舉動。黃老師把一個同學送給她的果籃裡面拿出一串誘人的葡萄。黃老師說：“今天是教師節，老師的內心很快樂。老師為什麼要把這串葡萄拿出來呢？今天老師就和你們一起來吃這串葡萄。”\n" +
                    "\n" +
                    "　　黃老師從第一組把葡萄送下來，到我了，那個葡萄酸酸甜甜的，很好吃。那個酸酸甜甜的味道就像我們之前經過的風風雨雨一樣。\n" +
                    "\n" +
                    "　　黃老師祝您工作順利，天天開心！\n" +
                    "\n" +
                    "　　廣西省欽州市永福小學五年級：黃子怡\n"+"<h1>这是一封注册验证码激活邮件,登录请输入以下验证码:" +code
                    +"</h1></body></html>"
                    ;
            message.setContent(content, "text/html;charset=UTF-8");
            // 3.发送邮件
            Transport.send(message);
            System.out.println("邮件成功发送!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}