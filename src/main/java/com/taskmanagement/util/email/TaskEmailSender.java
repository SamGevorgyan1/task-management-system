package com.taskmanagement.util.email;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Component;
import org.springframework.mail.javamail.JavaMailSender;

@Component
public class TaskEmailSender {

    @Autowired
    private  JavaMailSender mailSender;

    public void sendEmail(String to,String subject,String text){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("samg05981@gmail.com");
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }
}
