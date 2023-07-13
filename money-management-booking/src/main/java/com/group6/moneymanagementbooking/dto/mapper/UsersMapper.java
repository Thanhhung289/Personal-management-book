package com.group6.moneymanagementbooking.dto.mapper;

import java.util.ArrayList;
import java.util.List;

import com.group6.moneymanagementbooking.dto.request.UserDTOEditProfileRequest;
import com.group6.moneymanagementbooking.dto.request.UsersDTORegisterRequest;
import com.group6.moneymanagementbooking.dto.response.UsersDTOResponse;
import com.group6.moneymanagementbooking.dto.response.UsersForAdminDTOResponse;
import com.group6.moneymanagementbooking.enity.Users;

public class UsersMapper {
    public static UsersDTOResponse toUserDTOResponse(Users account) {
        return UsersDTOResponse.builder().firstName(account.getFirstName()).lastName(account.getLastName())
                .email(account.getEmail()).phone(account.getPhone()).address(account.getAddress())
                .role(account.getRole()).isActive(account.isActive()).currency(account.getCurrency())
                .annuallySpending(account.getAnnuallySpending()).monthlyEarning(account.getMonthlyEarning())
                .monthlySaving(account.getMonthlySaving()).monthlySpending(account.getMonthlySpending()).build();
    }

    public static Users toUsers(UsersDTORegisterRequest accountDTORegister) {
        return Users.builder().firstName("")
                .lastName("").email(accountDTORegister.getEmail())
                .password(accountDTORegister.getPassword()).currency("$").phone(accountDTORegister.getPhone())
                .active(true).nonLocked(true).address(accountDTORegister.getAddress())
                .active(true).role("ROLE_USER").annuallySpending(0).monthlySaving(0)
                .monthlySpending(0).monthlyEarning(0).build();
    }

    public static UsersForAdminDTOResponse toUsersForAdminDTOResponse(Users users) {
        return UsersForAdminDTOResponse.builder().id(users.getId())
                .name(users.getFirstName() + " " + users.getLastName())
                .email(users.getEmail()).phone(users.getPhone()).address(users.getAddress())
                .failedAttempt(users.getFailed_attempt()).role(users.getRole())
                .accountNonLocked(users.isNonLocked()).lockTime(users.getLockTime()).active(users.isActive()).build();
    }

      public static UserDTOEditProfileRequest toUserDTOEditProfileRequest(Users users){
        return UserDTOEditProfileRequest.builder().address(users.getAddress()).firstName(users.getFirstName()).id(users.getId())
        .lastName(users.getLastName()).email(users.getEmail()).avatar(users.getAvatar()).phone(users.getPhone()).build();
    }




  

    public static List<UsersForAdminDTOResponse> toUsersForAdminDTOResponses(List<Users> users) {
        List<UsersForAdminDTOResponse> list = new ArrayList<UsersForAdminDTOResponse>();
        for (Users user : users) {
            list.add(UsersMapper.toUsersForAdminDTOResponse(user));
        }

        return list;
    }

}
