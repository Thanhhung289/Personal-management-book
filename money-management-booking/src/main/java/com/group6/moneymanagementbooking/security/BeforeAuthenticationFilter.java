package com.group6.moneymanagementbooking.security;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.filter.OncePerRequestFilter;

import com.group6.moneymanagementbooking.dto.mapper.UsersMapper;
import com.group6.moneymanagementbooking.enity.Users;
import com.group6.moneymanagementbooking.service.AccountsService;
import com.group6.moneymanagementbooking.service.UsersService;
import com.group6.moneymanagementbooking.util.SecurityUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;

@Order(Ordered.LOWEST_PRECEDENCE)

public class BeforeAuthenticationFilter extends OncePerRequestFilter {
    private AccountsService accountsService;
    private UsersService usersService;

    public BeforeAuthenticationFilter(AccountsService accountsService, UsersService usersServiceImpl) {
        this.accountsService = accountsService;
        this.usersService = usersServiceImpl;
    }
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String email = SecurityUtils.getCurrentUsername();
        if (email != null && !email.equals("anonymousUser")) {
            double totalMoney = accountsService.getTotalBalance();
            request.setAttribute("totalMoney", totalMoney);
            Users u = usersService.getUserByEmail(email);
            request.setAttribute("currentUser",UsersMapper.toUserDTOResponse(u));
        }
        filterChain.doFilter(request, response);
    }
}