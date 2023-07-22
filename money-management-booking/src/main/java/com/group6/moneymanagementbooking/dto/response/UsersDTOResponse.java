package com.group6.moneymanagementbooking.dto.response;


import lombok.Builder;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UsersDTOResponse {
    private String fullName;

    private String email;

    private String phone;

    private String address;

    private String role;

    private String avatar;

    private boolean isActive;

    private double annuallySpending;

    private double monthlySpending;

    private double monthlySaving;

    private double monthlyEarning;

    private String currency;
    
}
