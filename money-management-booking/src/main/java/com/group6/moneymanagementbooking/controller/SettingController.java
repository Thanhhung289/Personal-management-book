package com.group6.moneymanagementbooking.controller;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.group6.moneymanagementbooking.dto.mapper.UsersMapper;
import com.group6.moneymanagementbooking.dto.request.UserDTOEditProfileRequest;
import com.group6.moneymanagementbooking.enity.Category;
import com.group6.moneymanagementbooking.enity.Users;
import com.group6.moneymanagementbooking.service.CategoryService;
import com.group6.moneymanagementbooking.service.UsersService;
import com.group6.moneymanagementbooking.util.PaginationUtil;
import com.group6.moneymanagementbooking.util.SecurityUtils;
import com.group6.moneymanagementbooking.util.WebUtils;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
@RequestMapping("/settings")
public class SettingController {
    private final CategoryService categoryService;
    private final UsersService usersService;

    @GetMapping(value = { "", "/index", "/" })
    public String setting(Model model, HttpServletRequest request) {
        return "setting";
    }

    @PostMapping("/add-category")
    public String addCategory(@ModelAttribute Category category, RedirectAttributes redirectAttributes) {
        categoryService.addCategory(category, redirectAttributes);
        return "redirect:/settings/category";
    }

    @GetMapping("/category")
    public String index(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "6") int pageSize,
            Model model, @ModelAttribute("mess") String mess) {
        model.addAttribute("mess", mess);
        model.addAttribute("listcategory", categoryService.findAll());
        model.addAttribute("record", categoryService.findAll().size());
        model.addAttribute("addcategory", new Category());
        Pageable pageable = PaginationUtil.getPageable(page, pageSize);
        List<Category> items = categoryService.findAll();
        Page<Category> itemsPage = PaginationUtil.paginate(pageable, items);
        model.addAttribute("page", itemsPage);
        return "list-category";
    }

    @PostMapping("/detail-category")
    public String detail(@ModelAttribute Category category) {
        return Optional.ofNullable(categoryService.updateCategory(category))
                .map(t -> "redirect:/settings/category")
                .orElse("failed");
    }

    @GetMapping("/delete-category/{id}")
    public String delete(@PathVariable("id") int id) {
        categoryService.deleteCategoryById(id);
        return "redirect:/settings/category";
    }

    @GetMapping("/profile")
    public String editProfile(Model model, HttpServletRequest request) {
        Users users = usersService.getUserByEmail(SecurityUtils.getCurrentUsername());
        model.addAttribute("userDTOEditProfile", UsersMapper.toUserDTOEditProfileRequest(users));
        return "user-profile";
    }

    @PostMapping("/profile")
    public String registerPost(Model model,
            @ModelAttribute("userDTOEditProfile") UserDTOEditProfileRequest userDTOEditProfile,
            HttpServletRequest request)
            throws Exception {
        String report = "Update success!!";
        try {
            usersService.updateInfo(userDTOEditProfile, request);
        } catch (Exception exception) {
            report = exception.getMessage();
        }
        return "redirect:/settings/profile/?report=" + report;
    }

    @PostMapping("/upload-avatar")
    public String uploadAvatar(HttpServletRequest request) {
        String avatar = request.getParameter("avatar");
        if (!avatar.isEmpty()) {
            usersService.uploadAvatar(avatar);
            return "redirect:/settings/profile"; // Chuyển hướng về trang profile

        }
        // Xử lý upload không thành công
        return "redirect:/settings/profile?report=image%20cannot%20be%20null!!";
    }

    @GetMapping("/change-password")
    public String changePasswordOnGet(Model model, HttpServletRequest request) {
        WebUtils.clearSession(request, "userRegister", "changePassword");
        return "change-password";
    }

    @PostMapping("/change-password")
    public String changePasswordOnPost(Model model, HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String[] listPass = new String[] { request.getParameter("oldPassword"), request.getParameter("newPassword"),
                request.getParameter("rePassword") };
        try {
            usersService.checkChangePassword(listPass);
        } catch (Exception e) {
            String report = e.getMessage();
            return "redirect:/settings/change-password?report=" + report;
        }
        HttpSession session = request.getSession();
        session.setAttribute("changePassword", request.getParameter("newPassword"));
        session.setAttribute("emailOTP", usersService.getUsers().getEmail());
        return "redirect:/otps/index";
    }


}
