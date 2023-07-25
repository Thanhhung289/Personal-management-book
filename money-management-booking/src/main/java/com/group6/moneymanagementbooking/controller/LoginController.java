package com.group6.moneymanagementbooking.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.group6.moneymanagementbooking.dto.request.UsersDTOForgotPasswordRequest;
import com.group6.moneymanagementbooking.dto.request.UsersDTOLoginRequest;
import com.group6.moneymanagementbooking.dto.request.UsersDTORegisterRequest;
import com.group6.moneymanagementbooking.service.UsersService;
import com.group6.moneymanagementbooking.util.CaptchaServiceUtils;
import com.group6.moneymanagementbooking.util.SecurityUtils;
import com.group6.moneymanagementbooking.util.WebUtils;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class LoginController {
    private final UsersService userService;

    @GetMapping("/login")
    public String goToLoginPage(Model model) {
        UsersDTOLoginRequest accountDTOLoginRequest = UsersDTOLoginRequest.builder().build();
        if (!SecurityUtils.getCurrentUsername().equals("anonymousUser")) {
            return "redirect:/users/overview";
        }
                model.addAttribute("site_key", CaptchaServiceUtils.SITE_KEY);
        model.addAttribute("usersDTOLoginRequest", accountDTOLoginRequest);
        model.addAttribute("report", "");
        return "login";
    }

    @GetMapping("/register")
    public String registerGet(Model model, HttpServletRequest request) {
        WebUtils.clearSession(request, "userRegister", "changePassword");
        UsersDTORegisterRequest usersDTORegister = UsersDTORegisterRequest.builder().build();
        model.addAttribute("usersDTORegister", usersDTORegister);
        model.addAttribute("site_key", CaptchaServiceUtils.SITE_KEY);
        return "register";
    }

    @PostMapping("/register")
    public String registerPost(RedirectAttributes redirectAttributes, HttpServletRequest request,
            @ModelAttribute("usersDTORegister") UsersDTORegisterRequest userDTORegister) throws Exception {
        List<String> report = new ArrayList<String>();
        userService.checkUserRegister(report, userDTORegister, request);
        if (!report.isEmpty()) {
            redirectAttributes.addAttribute("error", report);
            return "redirect:/register";
        }
        HttpSession session = request.getSession();
        session.setAttribute("emailOTP", userDTORegister.getEmail());
        session.setAttribute("userRegister", userDTORegister);

        return "redirect:/otps/index";
    }

    @GetMapping("/find-email")
    public String findEmail(Model model) {
        model.addAttribute("site_key", CaptchaServiceUtils.SITE_KEY);
        return "find-email";
    }

    @PostMapping("/find-email")
    public String findEmail(RedirectAttributes redirectAttributes, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        WebUtils.clearSession(request, "userRegister", "changePassword");
        String email = request.getParameter("email");
        if (userService.getUserByEmail(email) != null) {
            HttpSession session = request.getSession();
            session.setAttribute("emailOTP", email);
            return "redirect:/otps/index";
        }
        redirectAttributes.addAttribute("report", "not-found");
        return "redirect:/find-email";
    }

    @GetMapping("/forgot-password")
    public String resetPasswordGET(Model model, HttpServletRequest request) {

        UsersDTOForgotPasswordRequest usersDTOForgotPasswordRequest = UsersDTOForgotPasswordRequest.builder()
                .build();
        model.addAttribute("usersDTOForgotPasswordRequest", usersDTOForgotPasswordRequest);
        return "forgot-password";
    }

    @PostMapping("/forgot-password")
    public String resetPasswordPost(RedirectAttributes redirectAttributes, HttpServletRequest request,
            @ModelAttribute("usersDTOForgotPasswordRequest") UsersDTOForgotPasswordRequest usersDTOForgotPasswordRequest)
            throws Exception {
        HttpSession session = request.getSession();
        String email = (String) session.getAttribute("emailOTP");
        usersDTOForgotPasswordRequest.setEmail(email);
        List<String> report = new ArrayList<>();
        userService.checkForgotPassword(report, usersDTOForgotPasswordRequest);
        if (report.size() == 0) {
            session.removeAttribute("emailOTP");
            return "redirect:/login";
        }
        redirectAttributes.addAttribute("report", report);
        return "redirect:/forgot-password";
    }

}
