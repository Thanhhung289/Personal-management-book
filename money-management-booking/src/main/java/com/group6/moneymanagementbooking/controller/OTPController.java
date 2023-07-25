package com.group6.moneymanagementbooking.controller;

import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.mail.EmailException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.group6.moneymanagementbooking.dto.request.UsersDTORegisterRequest;
import com.group6.moneymanagementbooking.enity.OTP;
import com.group6.moneymanagementbooking.service.EmailService;
import com.group6.moneymanagementbooking.service.OTPService;
import com.group6.moneymanagementbooking.service.UsersService;
import com.group6.moneymanagementbooking.util.NumberUtils;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/otps")
@RequiredArgsConstructor
public class OTPController {
    private final OTPService otpService;
    private final EmailService emailService;
    private final UsersService usersService;

    @GetMapping(value = "/sendOTP")
    @ResponseStatus(value = HttpStatus.OK)
    public void sendOTPMail(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        HttpSession session = request.getSession();
        if (session.getAttribute("emailOTP") != null) {
            String email = (String) session.getAttribute("emailOTP");
            int otpCode = NumberUtils.getRandomInt(100000, 999999);
            String htmlContent = otpService.OTPEmailTemplate(String.valueOf(otpCode));
            try {
                emailService.sendVerifyEmail(email, "Dear MyFriend, ", htmlContent);
                System.out.println("Mail sent successfully.");
            } catch (EmailException e) {
                throw new Exception(e.getMessage());
            }
            OTP otp = OTP.builder().date_create(LocalDateTime.now()).email(email).code(String.valueOf(otpCode)).build();
            otpService.saveOTP(otp);
        } else {
            throw new Exception("Không tồn tại email");
        }

    }

    @GetMapping(value = { "", "/", "index" })
    public String OTPPage(HttpServletRequest request) {

        return "otp";
    }

    @PostMapping(value = "/confirm")
    public void confirmOTP(HttpServletRequest request, HttpServletResponse response, RedirectAttributes model)
            throws Exception {
        HttpSession session = request.getSession(false);
        String userEmailOTP = (String) session.getAttribute("emailOTP");
        String otpUserInput = request.getParameter("otp");
        PrintWriter out = response.getWriter();
        String report = "";
        try {
            otpService.confirm(userEmailOTP, otpUserInput);
            register(session, userEmailOTP);
            changePassword(userEmailOTP, session);
        } catch (Exception e) {
            report = e.getMessage();
            out.println(e.getMessage());
        }
        if (report.isEmpty()) {
            if (session.getAttribute("userRegister") == null && session.getAttribute("changePassword") == null) {
                out.println("resetPassword");
            } else {
                session.removeAttribute("userRegister");
                session.removeAttribute("changePassword");
                session.removeAttribute("emailOTP");
                out.println("login");
            }
        }
    }

    private void register(HttpSession session, String userEmailOTP) {
        if (session.getAttribute("userRegister") != null) {
            UsersDTORegisterRequest usersDTORegisterRequest = (UsersDTORegisterRequest) session
                    .getAttribute("userRegister");
            if (userEmailOTP.equals(usersDTORegisterRequest.getEmail())) {
                usersService.addUser(usersDTORegisterRequest);
            }
        }
    }

    private void changePassword(String userEmailOTP, HttpSession session) {
        if (session.getAttribute("changePassword") != null) {
            usersService.changePassword((String) session.getAttribute("changePassword"));
        }
    }

}
