package com.group6.moneymanagementbooking.service.impl;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import com.group6.moneymanagementbooking.dto.mapper.UsersMapper;
import com.group6.moneymanagementbooking.dto.request.UsersDTOForgotPasswordRequest;
import com.group6.moneymanagementbooking.dto.request.UsersDTOLoginRequest;
import com.group6.moneymanagementbooking.dto.request.UsersDTORegisterRequest;
import com.group6.moneymanagementbooking.enity.Users;
import com.group6.moneymanagementbooking.repository.UsersRepository;
import com.group6.moneymanagementbooking.service.UsersService;
import com.group6.moneymanagementbooking.util.SecurityUtils;
import com.group6.moneymanagementbooking.util.StringUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UsersServiceImpl implements UsersService {
    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;
    private final int MAX_FAILED_ATTEMPTS = 3;
    private final long LOCK_TIME_DURATION = 24 * 60 * 60 * 1000;

    // register
    @Override
    public String userRegister(Model model, UsersDTORegisterRequest accountDTORegister) throws Exception {
        String report = "<p style='padding-left:20px; height: 100%; line-height:100%;' > Warning: ";
        int report_length = report.length();
        report += registerCheckCondition(accountDTORegister);
        if (report.length() > report_length) {
            report += "</p>";
            model.addAttribute("report", report);
            model.addAttribute("accountDTORegister", accountDTORegister);
            return "register";
        }
        Users account = UsersMapper.toUsers(accountDTORegister);
        account.setPassword(passwordEncoder.encode(account.getPassword()));
        account = usersRepository.save(account);
        UsersDTOLoginRequest usersDTOLoginRequest = UsersDTOLoginRequest.builder().email(accountDTORegister.getEmail())
                .build();
        model.addAttribute("usersDTOLoginRequest", usersDTOLoginRequest);
        return "redirect:/login";
    }

    // forgot_password
    @Override
    public String forgotPassword(Model model, UsersDTOForgotPasswordRequest usersDTOForgotPasswordRequest) {
        String email = usersDTOForgotPasswordRequest.getEmail();
        Optional<Users> usersOptional = usersRepository.findByEmail(email);
        if (usersOptional.isPresent()) {
            Users users = usersOptional.get();
            if (usersDTOForgotPasswordRequest.getPassword().equals(usersDTOForgotPasswordRequest.getRepeatPassword())) {
                users.setPassword(passwordEncoder.encode(usersDTOForgotPasswordRequest.getPassword()));
                usersRepository.save(users);
            } else {
                String report = "<p style='padding-left:20px; height: 100%; line-height:100%; font-size:12px;' > Warning: Password and re-password are not the same </p>";
                model.addAttribute("report", report);
                return "forgot-password";
            }
        } else {
            String report = "<p style='padding-left:20px; height: 100%; line-height:100%;' > Warning: Your account not exits </p>";
            model.addAttribute("report", report);
            return "forgot-password";
        }
        UsersDTOLoginRequest usersDTOLoginRequest = UsersDTOLoginRequest.builder().email(email).build();
        model.addAttribute("usersDTOLoginRequest", usersDTOLoginRequest);
        return "login";
    }

    // get user by email
    @Override
    public Users getUserByEmail(String email) {
        return (usersRepository.findByEmail(email)).get();
    }

    // check_condition
    // 1. check email condition
    @Override
    public void checkEmailCondition(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String userEmail = request.getParameter("userEmail");
        try (PrintWriter out = response.getWriter()) {
            if (StringUtils.patternMatchesEmail(userEmail,
                    "^([\\w-\\.]+){1,64}@([\\w&&[^_]]+){2,255}(.[a-z]{2,3})+$|^$")) {
                if (checkEmailDuplicate(userEmail)) {
                    out.println("<h5 style='color:red;'>Your email already exist!!!</h5>");
                } else {
                    out.println("<h5 style='color:green;'>Your email address is accepted!!!</h5>");
                }
            } else {
                out.println("<h5 style='color:red;'>Your email is invalid!!!</h5>");
            }
        }
    }

    // 2. check phone condition
    @Override
    public void checkPhoneCondition(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String userPhone = request.getParameter("userPhone");
        try (PrintWriter out = response.getWriter()) {
            if (!StringUtils.isDigit(userPhone)) {
                out.println("<h5 style='color:red;'>Your phone number must be digit!!!</h5>");
            } else {
                if (userPhone.length() > 10 || userPhone.length() < 10) {
                    out.println("<h5 style='color:red;'>Your phone number must have 10 digit!!!</h5>");
                } else {
                    if (checkPhoneDuplicate(userPhone)) {
                        out.println("<h5 style='color:red;'>Your phone number already exist!!!</h5>");
                    } else {
                        out.println("<h5 style='color:green;'>Your phone number is accepted!!!</h5>");
                    }
                }
            }
        }
    }

    // function for security
    public void increaseFailedAttempt(Users users) {
        int fa = users.getFailed_attempt() + 1;
        users.setFailed_attempt(fa);
        usersRepository.save(users);
    }

    public void lock(Users users) {
        users.setNonLocked(false);
        users.setLockTime(new Date());
        usersRepository.save(users);
    }

    public boolean unlock(Users users) {
        long lockTimeInMillis = users.getLockTime().getTime();
        long currentTimeInMillis = System.currentTimeMillis();
        if (lockTimeInMillis + LOCK_TIME_DURATION < currentTimeInMillis) {
            users.setNonLocked(true);
            users.setLockTime(null);
            users.setFailed_attempt(0);
            return true;
        }
        return false;
    }

    public void checkUnLockUser(Users users, HttpServletResponse response) throws IOException {
        if (users.getFailed_attempt() < (MAX_FAILED_ATTEMPTS - 1)) {
            increaseFailedAttempt(users);
            response.sendRedirect("/login?error=login-fail&turn=" + (MAX_FAILED_ATTEMPTS - users.getFailed_attempt()));
        } else {
            lock(users);
            response.sendRedirect("/login?error=login-fail&turn=0");
        }
    }

    // support function
    private boolean checkPhoneDuplicate(String phone) {
        Optional<Users> accouOptional = usersRepository.findByPhone(phone);
        if (accouOptional.isPresent()) {
            return true;
        }
        return false;
    }

    private boolean checkEmailDuplicate(String email) {
        Optional<Users> accouOptional = usersRepository.findByEmail(email);
        if (!accouOptional.isPresent()) {

            return false;
        }
        return true;
    }

    private String registerCheckCondition(UsersDTORegisterRequest accountDTORegister) {
        String report = "";
        if (checkEmailDuplicate(accountDTORegister.getEmail())) {
            report += "This email already exits!! </br>";
        }
        if (checkPhoneDuplicate(accountDTORegister.getPhone())) {
            report += "This phone already in use!! </br>";
        }
        if (!accountDTORegister.getPassword().equals(accountDTORegister.getRepeatPassword())) {
            report += "Password and password is different!!! </br>";
        }
        if (accountDTORegister.getPassword().isEmpty() || accountDTORegister.getRepeatPassword().isEmpty()) {
            report += "Password/Re-password cannot be empty!!! </br>";
        }
        return report;
    }

    @Override
    public Users getUsers() {
        return usersRepository.findByEmail(SecurityUtils.getCurrentUsername()).get();
    }

    @Override
    public void addAdjustForUser(Users users, Model model) {
        try {
            Optional<Users> existingUser = usersRepository.findByEmail(SecurityUtils.getCurrentUsername());
            if (existingUser.isPresent()) {
                Users userToUpdate = existingUser.get();
                double monthlySpending = users.getMonthlySpending();
                double monthlyEarning = users.getMonthlyEarning();
                double monthlySaveing = users.getMonthlySaving();
                double getAnnuallySpending = users.getAnnuallySpending();
                if (monthlySpending < 0)
                    throw new Exception("monthlySpending must be greater than 0");
                if (monthlyEarning < 0)
                    throw new Exception("monthlyEarning must be greater than 0");
                if (monthlySaveing > monthlyEarning - monthlySpending) {
                    throw new Exception("Monthly Saving set must be small or equal to the money earned minus spending");
                }
                userToUpdate.setAnnuallySpending(getAnnuallySpending);
                userToUpdate.setMonthlySpending(monthlySpending);
                userToUpdate.setMonthlySaving(monthlySaveing);
                userToUpdate.setMonthlyEarning(monthlyEarning);
                usersRepository.save(userToUpdate);
            }
        } catch (Exception e) {
            model.addAttribute("mess", e.getMessage());
        }
    }

}
