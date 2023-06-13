package com.group6.moneymanagementbooking.controller;



import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.group6.moneymanagementbooking.model.exception.custom.CustomBadRequestException;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admins")
@CrossOrigin
public class AdminController {
    @GetMapping("/home")
    public String getLoginFunction(Model model) throws CustomBadRequestException {
        return "index";
    }
}