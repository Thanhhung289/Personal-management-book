package com.group6.moneymanagementbooking.service.impl;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import com.group6.moneymanagementbooking.dto.mapper.UsersMapper;
import com.group6.moneymanagementbooking.dto.request.UserDTOEditProfileRequest;
import com.group6.moneymanagementbooking.dto.request.UsersDTOForgotPasswordRequest;
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
    private final int MAX_FAILED_ATTEMPTS = 5;
    private final int LOCK_TIME_DURATION = 24 * 60 * 60 * 1000;

    // register
    @Override
    public void checkUserRegister(List<String> report, UsersDTORegisterRequest userDTORegister,
            HttpServletRequest request) throws Exception {
        String pass = userDTORegister.getPassword();
        if (userDTORegister.getEmail() == null || userDTORegister.getPassword() == null
                || userDTORegister.getRepeatPassword() == null) {
            report.add("Warning: Data cannot be null!!");
            return;
        }
        if(userDTORegister.getEmail().isEmpty() || userDTORegister.getPassword().isEmpty() || userDTORegister.getRepeatPassword().isEmpty()){
            report.add("Warning: Data cannot be empty");
            return;
        }
        if (checkEmailDuplicate(userDTORegister.getEmail())) {
            report.add("Warning: This email already exits!!");
        }
        if (!StringUtils.isValidEmail(userDTORegister.getEmail())) {
            report.add("Warning: This email is not valid!!");
        }
        if (!StringUtils.checkPasswordValidate(pass)) {
            report.add("Warning: Password must conform to regex!!");
        }
        if (!userDTORegister.getPhone().isEmpty() &&
                !StringUtils.checkPhone(userDTORegister.getPhone())) {
            report.add("Warning: Phone number must conform to regex!!");
        }
        if (!pass.equals(userDTORegister.getRepeatPassword())) {
            report.add("Warning: Password and re-password are not the same!!");
        }
    }

    @Override
    public void addUser(UsersDTORegisterRequest userDTORegister) {
        Users user = UsersMapper.toUsers(userDTORegister);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user = usersRepository.save(user);
    }

    // update avatar
    @Override
    public void uploadAvatar(String avatar) {
        Users users = usersRepository.findByEmail(SecurityUtils.getCurrentUsername()).get();
        users.setAvatar(avatar);
        usersRepository.save(users);
    }

    // update_user
    @Override
    public void updateInfo(UserDTOEditProfileRequest userDTOEditProfile, HttpServletRequest request) throws Exception {
        Optional<Users> usersOptional = usersRepository.findByEmail(userDTOEditProfile.getEmail());
        if (userDTOEditProfile.getFirstName().isEmpty() || userDTOEditProfile.getLastName().isEmpty() || userDTOEditProfile.getPhone().isEmpty()) {
            throw new Exception("Warning: Data cannot be empty!!");
        }
        if (usersOptional.isPresent()) {
            Users users = usersOptional.get();
            users.setAddress(userDTOEditProfile.getAddress());
            users.setFirstName(userDTOEditProfile.getFirstName());
            users.setLastName(userDTOEditProfile.getLastName());
            users.setPhone(userDTOEditProfile.getPhone());
            try {
                usersRepository.save(users);
            } catch (Exception e) {
                throw new Exception("Warning: Update Infor: " + e.getMessage());
            }

        } else {
            throw new Exception("Warning: Data cannot be null");
        }
    }

    // change_password
    @Override
    public void checkChangePassword(String[] listPass) throws Exception {
        if (listPass[0].isEmpty() || listPass[1].isEmpty() || listPass[2].isEmpty()) {
            throw new Exception("Warning: Input cannot be empty");
        }
        if (listPass[0].equals(listPass[1])) {
            throw new Exception("Warning: This new password is the same with your old password");
        }
        if (!passwordEncoder.matches(listPass[0], getUsers().getPassword())) {
            throw new Exception("Warning: Old password is incorrect");
        }
        if (!listPass[1].equals(listPass[2])) {
            throw new Exception("Warning: Password and re-password are not the same");
        }
        if (!StringUtils.checkPasswordValidate(listPass[1])) {
            throw new Exception("Warning: Password must conform to regex");
        }
    }

    @Override
    public void changePassword(String newPassword) {
        Users users = getUsers();
        users.setPassword(passwordEncoder.encode(newPassword));
        usersRepository.save(users);
    }

    // forgot_password
    @Override
    public void checkForgotPassword(List<String> report, UsersDTOForgotPasswordRequest usersDTOForgotPasswordRequest) {
        if(usersDTOForgotPasswordRequest.getPassword() == null || usersDTOForgotPasswordRequest.getRepeatPassword() == null || usersDTOForgotPasswordRequest.getEmail() == null){
            report.add("Warning: Data cannot be null");
            return;
        }
             if(usersDTOForgotPasswordRequest.getPassword().isEmpty()|| usersDTOForgotPasswordRequest.getRepeatPassword().isEmpty() ){
            report.add("Warning: Data cannot be empty");
            return;
        }
        String email = usersDTOForgotPasswordRequest.getEmail();
        Optional<Users> usersOptional = usersRepository.findByEmail(email);
        if (usersOptional.isPresent()) {
            Users users = usersOptional.get();
            if (!StringUtils.checkPasswordValidate(usersDTOForgotPasswordRequest.getPassword())) {
                report.add(" Warning: Your new password must conform regex");
            }
            if (!usersDTOForgotPasswordRequest.getPassword()
                    .equals(usersDTOForgotPasswordRequest.getRepeatPassword())) {
                report.add(" Warning: Password and re-password are not the same");
            }
            if (usersDTOForgotPasswordRequest.getPassword().equals(users.getPassword())) {
                report.add(" Warning: This new password is the same with your old password");
            }
            if (report.size() == 0) {
                users.setPassword(passwordEncoder.encode(usersDTOForgotPasswordRequest.getPassword()));
                usersRepository.save(users);
            }
        } else {
            report.add("Warning: Your account not exits");
        }

    }

    // get user by email
    @Override
    public Users getUserByEmail(String email) {
        if (usersRepository.findByEmail(email).isPresent()) {
            return usersRepository.findByEmail(email).get();
        }
        return null;
    }

    // check_condition
    // 1. check email condition
    @Override
    public void checkEmailCondition(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String userEmail = request.getParameter("userEmail");
        try (PrintWriter out = response.getWriter()) {
            if (!userEmail.isEmpty() && StringUtils.patternMatchesEmail(userEmail,
                    "^([\\w-\\.]+){1,64}@([\\w&&[^_]]+){2,255}(.[a-z]{2,3})+$|^$")) {
                if (checkEmailDuplicate(userEmail)) {
                    out.println("<p class='alert alert-danger text-center mess' >Your email already exist!!!</p>");
                } else {
                    out.println(
                            "<p class='alert alert-success text-center mess' >Your email address is accepted!!!</p>");
                }
            } else {
                out.println("<p  class='alert alert-danger text-center mess'>Your email is invalid!!!</p>");
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
            usersRepository.save(users);
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
    private boolean checkEmailDuplicate(String email) {
        Optional<Users> accouOptional = usersRepository.findByEmail(email);
        if (!accouOptional.isPresent()) {

            return false;
        }
        return true;
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
