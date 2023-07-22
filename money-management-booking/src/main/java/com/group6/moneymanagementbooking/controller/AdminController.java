package com.group6.moneymanagementbooking.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.group6.moneymanagementbooking.dto.mapper.UsersMapper;
import com.group6.moneymanagementbooking.dto.request.UsersDTORegisterRequest;
import com.group6.moneymanagementbooking.dto.response.UsersForAdminDTOResponse;
import com.group6.moneymanagementbooking.enity.Users;
import com.group6.moneymanagementbooking.repository.UsersRepository;
import com.group6.moneymanagementbooking.service.AdminService;
import com.group6.moneymanagementbooking.util.WebUtils;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admins")
@CrossOrigin
public class AdminController {
    private final AdminService adminService;
    private final UsersRepository usersRepository;
    private final static String HOME = "admin-home";

    @GetMapping(value = { "", "/home", "/index", "/" })
    public String adminHomePage(Model model) {
        List<UsersForAdminDTOResponse> groupOfUsers = adminService.getPageGroupOfUsers(model, 1);
        return WebUtils.adminDispartcher(HOME, model, groupOfUsers, 1, null, null);
    }

    @GetMapping("/home/{page}")
    public String adminHomePage(@PathVariable("page") String pagging, Model model) {
        int page = Integer.parseInt(pagging.substring(4, pagging.length()));
        List<UsersForAdminDTOResponse> groupOfUsers = adminService.getPageGroupOfUsers(model, page);
        return WebUtils.adminDispartcher(HOME, model, groupOfUsers, page, null, null);
    }

    @GetMapping("/search")
    public String search(Model model, @RequestParam("select") String searchBy, @RequestParam("value") String value,
            @RequestParam(value = "nonlocked", required = false) Boolean nonlocked,
            @RequestParam(value = "isactive", required = false) Boolean isActive) {
        if (searchBy.equals("select")) {
            return "redirect:/admins/home";
        } else {
            model.addAttribute("select", searchBy);
            model.addAttribute("searchValue", value);
        }
        List<UsersForAdminDTOResponse> groupOfUsers = null;
        if (nonlocked != null) {
            groupOfUsers = adminService.searchInGroupOfLockedUsers(model, searchBy, value, 1, !nonlocked);
            return WebUtils.adminDispartcher(HOME, model, groupOfUsers, 1, null, !nonlocked);
        }
        if (isActive != null) {
            groupOfUsers = adminService.searchInGroupOfActiveUsers(model, searchBy, value, 1, !isActive);
            return WebUtils.adminDispartcher(HOME, model, groupOfUsers, 1, !isActive, null);
        }
        groupOfUsers = adminService.searchUsers(model, searchBy, value, 1);
        return WebUtils.adminDispartcher(HOME, model, groupOfUsers, 1, null, null);
    }

    @GetMapping("/search/{page}")
    public String search(Model model, @PathVariable("page") String pagging,
            @RequestParam("select") String searchBy, @RequestParam("value") String value,
            @RequestParam(value = "nonlocked", required = false) Boolean nonlocked,
            @RequestParam(value = "isactive", required = false) Boolean isActive) {
        int page = Integer.parseInt(pagging.substring(4, pagging.length()));
        List<UsersForAdminDTOResponse> groupOfUsers = null;
        model.addAttribute("select", searchBy);
        model.addAttribute("searchValue", value);

        if (nonlocked != null) {
            groupOfUsers = adminService.searchInGroupOfLockedUsers(model, searchBy, value, page, !nonlocked);
            return WebUtils.adminDispartcher(HOME, model, groupOfUsers, page, null, !nonlocked);
        }
        if (isActive != null) {
            groupOfUsers = adminService.searchInGroupOfActiveUsers(model, searchBy, value, page, !isActive);
            return WebUtils.adminDispartcher(HOME, model, groupOfUsers, page,
                    !isActive, null);
        }
        groupOfUsers = adminService.searchUsers(model, searchBy, value, page);
        return WebUtils.adminDispartcher(HOME, model, groupOfUsers, page,
                null, null);
    }

    @GetMapping("/change-status/{id}")
    public void changeUserActiveStatus(HttpServletResponse response, Model model, @PathVariable("id") String userId)
            throws IOException {
        int id = Integer.parseInt(userId);
        adminService.changeActiveStatus(response, id);
    }

    @GetMapping("/list-locked")
    public String getGroupsOfLockedUsers(@RequestParam("nonlocked") Boolean isLocked, Model model) {
        List<UsersForAdminDTOResponse> groupOfUsers = adminService.getGroupOfLockedUser(model, isLocked, 1);
        return WebUtils.adminDispartcher(HOME, model, groupOfUsers, 1, null, !isLocked);
    }

    @GetMapping("/list-locked/{page}")
    public String getGroupsOfLockedUsers(@RequestParam("nonlocked") Boolean isLocked, Model model,
            @PathVariable("page") String pagging) {
        int page = Integer.parseInt(pagging.substring(4, pagging.length()));
        List<UsersForAdminDTOResponse> groupOfUsers = adminService.getGroupOfLockedUser(model, isLocked, page);
        return WebUtils.adminDispartcher(HOME, model, groupOfUsers, page, null, !isLocked);
    }

    @GetMapping("/list-status")
    public String getGroupOfActiveUsers(@RequestParam("isactive") Boolean isActive, Model model) {
        List<UsersForAdminDTOResponse> groupOfUsers = adminService.getGroupOfActiveUsers(model, isActive, 1);
        return WebUtils.adminDispartcher(HOME, model, groupOfUsers, 1, !isActive, null);
    }

    @GetMapping("/list-status/{page}")
    public String getGroupOfActiveUsers(@RequestParam("isactive") Boolean isActive, Model model,
            @PathVariable("page") String pagging) {
        int page = Integer.parseInt(pagging.substring(4, pagging.length()));
        List<UsersForAdminDTOResponse> groupOfUsers = adminService.getGroupOfActiveUsers(model, isActive, page);
        return WebUtils.adminDispartcher(HOME, model, groupOfUsers, page, !isActive, null);
    }

    @GetMapping("/addfast")
    public String addFast() {
        for (int i = 101; i <= 200; i++) {
            String firstName = "trong"+i;
            String lastName = "nguyen" + i;
            String email = "trongnguyen" + i + "@gmail.com";
            String password = "$2a$10$tCfT.g3xn99ISNlniDJy/ehNibU5vCqKS.5nDWtmpfozWHpOHo/fu";
            String address = "Ninh Binh";
            String phone = String.valueOf(1000000000 + i);
            UsersDTORegisterRequest usersDTORegisterRequest = new UsersDTORegisterRequest(email,
                    password, password, phone, address);
            Users users = UsersMapper.toUsers(usersDTORegisterRequest);
            users.setFirstName(firstName);
            users.setLastName(lastName);
            usersRepository.save(users);
        }
        String statusId = "#status" + 100;
        return "redirect:/admins/home/" + statusId;
    }

    // private
}
