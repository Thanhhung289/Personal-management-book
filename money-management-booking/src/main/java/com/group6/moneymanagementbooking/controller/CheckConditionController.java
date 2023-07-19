package com.group6.moneymanagementbooking.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.group6.moneymanagementbooking.service.UsersService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/check")
@CrossOrigin
public class CheckConditionController {
    private final UsersService userService;

    @GetMapping("/email")
    @ResponseStatus(value = HttpStatus.OK)
    public void checkEmail(HttpServletRequest request, HttpServletResponse response) throws Exception {
        userService.checkEmailCondition(request, response);
    }

}
