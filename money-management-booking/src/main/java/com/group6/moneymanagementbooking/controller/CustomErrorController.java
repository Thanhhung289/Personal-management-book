package com.group6.moneymanagementbooking.controller;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CustomErrorController implements ErrorController {

    private static final String ERROR_PATH = "/error";

    @RequestMapping(ERROR_PATH)
    public String handleError(HttpServletRequest request, Model model) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        if (status != null) {
            Integer statusCode = Integer.valueOf(status.toString());
            if (statusCode == HttpStatus.FORBIDDEN.value()) {
                return "errors/403";
            }
            if (statusCode == HttpStatus.NOT_FOUND.value()) {
                return "errors/404";
            }
            if(statusCode == HttpStatus.UNAUTHORIZED.value()){
                return "errors/401";
            }
            if(statusCode == HttpStatus.BAD_REQUEST.value()){
                return "errors/400";
            }
        }
      return "errors/500";

    }

    @RequestMapping("/errors/403")
    public String handleAccessDenied(Model model) {
        return "errors/403";
    }

}
