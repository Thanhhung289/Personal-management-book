package com.group6.moneymanagementbooking.security;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.filter.OncePerRequestFilter;

import com.group6.moneymanagementbooking.util.CaptchaServiceUtils;

import java.io.IOException;

public class CaptchaAuthenticationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String[] captchaVerifyURL = { "/login", "/register", "/find-email" };
        if (isLoginRequest(request, captchaVerifyURL) && isPostMethod(request)) {
            String captchaResponse = request.getParameter("g-recaptcha-response");

            if (captchaResponse == null || captchaResponse.isEmpty() || !CaptchaServiceUtils.verify(captchaResponse)) {
                if (request.getRequestURI().equals("/login")) {
                    response.sendRedirect(request.getRequestURI() + "?error=captcha");
                    return;
                } else {
                    response.sendRedirect(request.getRequestURI() + "?error=Warning: Captcha is not verify");
                    return;
                }
            }
        }
        filterChain.doFilter(request, response);
    }

    private boolean isLoginRequest(HttpServletRequest request, String[] captchaVerifyURL) {
        for (String string : captchaVerifyURL) {
            if (string.equals(request.getRequestURI())) {
                return true;
            }
        }
        return false;
    }

    private boolean isPostMethod(HttpServletRequest request) {
        return "POST".equalsIgnoreCase(request.getMethod());
    }
}
