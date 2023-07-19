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
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String loginUrl = "/login";
        if (isLoginRequest(request, loginUrl) && isPostMethod(request)) {
            String captchaResponse = request.getParameter("g-recaptcha-response");

            if (captchaResponse == null || captchaResponse.isEmpty() || !CaptchaServiceUtils.verify(captchaResponse)) {
                response.sendRedirect(loginUrl + "?error=captcha");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private boolean isLoginRequest(HttpServletRequest request, String loginUrl) {
        return loginUrl.equals(request.getRequestURI());
    }

    private boolean isPostMethod(HttpServletRequest request) {
        return "POST".equalsIgnoreCase(request.getMethod());
    }
}

