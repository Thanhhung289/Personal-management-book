package com.group6.moneymanagementbooking.service;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Async
    public void sendVerifyEmail(String to, String subject, String body) throws Exception {
        try {
            HtmlEmail email = new HtmlEmail();
            email.setHostName("smtp.gmail.com");
            email.setSmtpPort(587);
            email.setAuthenticator(new DefaultAuthenticator("nguyntrng234@gmail.com", "kubbxerhqhoazngp"));
            email.setStartTLSEnabled(true);
            email.setFrom("nguyntrng234@gmail.com");
            email.addTo(to);
            email.setSubject(subject);
            email.setHtmlMsg(body);
            email.send();
        } catch (EmailException e) {
            throw new Exception(e.getMessage());
        }

    }

}