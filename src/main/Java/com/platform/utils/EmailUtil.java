package com.platform.utils;

import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;


public class EmailUtil {

    private static String username = PropertiesUtil.getProperty("email.username");
    private static String password = PropertiesUtil.getProperty("email.password");
    private static String hostname = PropertiesUtil.getProperty("email.hostname");
    private static final Integer smtpPort = Integer.parseInt(PropertiesUtil.getProperty("email.smtp.port"));
    private static String showname = PropertiesUtil.getProperty("email.showname");

    //测试
//    public static void main(String[] args) throws EmailException {
//        String htmlMsg = "<html>hello everyone <a href='http://www.bilibili.com'>go!</a></html>";
//        sendEmail("meanlgp@163.com", "跳转Blibili", htmlMsg);
//    }

    // QQ邮箱授权码:mmpssamfisoedddi
    public static void sendEmail(String userEmail, String subject, String htmlMsg) throws EmailException {
        HtmlEmail email = new HtmlEmail();

        // 设置smtp服务器, 如"smtp.qq.com"
        email.setHostName(hostname);

        System.out.println(smtpPort);
        // 设置端口,默认端口是25
        email.setSmtpPort(smtpPort);

        // SSL enabled SMTP server,即如果是支持SSL服务器的端口是465
        // simpleEmail.setSmtpPort(465);//gmail邮箱服务器就是支持SSL的。

        // 设置登入认证服务器的用户名和密码
        email.setAuthentication(username, password);

        // 设置编码格式
        email.setCharset("utf-8");

        // 设置收件人, 可以是多个
        email.addTo(userEmail);

        // 设置发送人邮件和名字
        email.setFrom(username, showname);

        // 设置邮件主题
        email.setSubject(subject);

        // 如果没有发送html邮件, 那么发送文本邮件
        // 设置邮件内容
        // set the html message
        email.setHtmlMsg(htmlMsg);

        // set the alternative message
        email.setTextMsg("Your email client does not support HTML messages");

        // 发送邮件
        email.send();
    }

}
