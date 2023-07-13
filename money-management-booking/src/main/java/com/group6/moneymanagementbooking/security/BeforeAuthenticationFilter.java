package com.group6.moneymanagementbooking.security;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.filter.OncePerRequestFilter;

import com.group6.moneymanagementbooking.service.AccountsService;
import com.group6.moneymanagementbooking.util.SecurityUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
@Order(Ordered.HIGHEST_PRECEDENCE)

public class BeforeAuthenticationFilter extends OncePerRequestFilter {
   private  AccountsService accountsService;
    public BeforeAuthenticationFilter(AccountsService accountsService){
        this.accountsService = accountsService;
    }
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
                String email = SecurityUtils.getCurrentUsername();
                if(email != null){
                    double totalMoney = accountsService.getTotalBalance();
                    request.setAttribute("totalMoney", totalMoney);
                }
        
        // Tiếp tục xử lý các filter và controller tiếp theo
        filterChain.doFilter(request, response);
    }
}