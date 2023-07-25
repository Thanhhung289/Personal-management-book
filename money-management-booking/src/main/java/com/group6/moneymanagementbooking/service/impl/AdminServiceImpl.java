package com.group6.moneymanagementbooking.service.impl;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletResponse;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import com.group6.moneymanagementbooking.dto.mapper.UsersMapper;

import com.group6.moneymanagementbooking.dto.response.UsersForAdminDTOResponse;
import com.group6.moneymanagementbooking.enity.Users;
import com.group6.moneymanagementbooking.repository.UsersRepository;
import com.group6.moneymanagementbooking.service.AdminService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {
    private final UsersRepository usersRepository;
    private final int PAGE_SIZE = 8;

    // unlock user
    @Override
    public void unlockUser(int id) throws Exception {
        Optional<Users> optional = usersRepository.findById(id);
        if (optional.isPresent()) {
            Users user = optional.get();
            user.setNonLocked(true);
            user.setFailed_attempt(0);
            user.setLockTime(null);
            usersRepository.save(user);
        } else {
            throw new Exception("User not exist!!");
        }
    }

    // getting
    // 1. get group of users original
    @Override
    public List<UsersForAdminDTOResponse> getPageGroupOfUsers(Model model, int page) {
        int totalPage = getMaxPage();
        if (page > totalPage)
            page = totalPage;
        if (totalPage == 0)
            page = 1;
        model.addAttribute("data2", totalPage);
        List<Users> list = usersRepository.findByRole("ROLE_USER", PageRequest.of(page - 1, PAGE_SIZE)).toList();
        return UsersMapper.toUsersForAdminDTOResponses(list);
    }

    // 2. get group of locked/nonlock users
    @Override
    public List<UsersForAdminDTOResponse> getGroupOfLockedUser(Model model, Boolean islock, int page) {
        int totalPage = getSearchMaxPage("locked", islock.toString());
        if (page >= totalPage)
            page = totalPage;
        if (totalPage == 0)
            page = 1;
        List<Users> listOfLockedUser = usersRepository
                .findByNonLockedAndRole(islock, "ROLE_USER", PageRequest.of((page - 1), PAGE_SIZE))
                .toList();
        model.addAttribute("data2", totalPage);
        return UsersMapper.toUsersForAdminDTOResponses(listOfLockedUser);
    }

    // 3. get group of active/inactive users
    @Override
    public List<UsersForAdminDTOResponse> getGroupOfActiveUsers(Model model, Boolean isActive, int page) {
        int totalPage = getSearchMaxPage("active", isActive.toString());
        if (page >= totalPage) {
            page = totalPage;
        }
        if (totalPage == 0)
            page = 1;
        List<Users> listOfActiveUser = usersRepository
                .findByActiveAndRole(isActive, "ROLE_USER", PageRequest.of((page - 1), PAGE_SIZE))
                .toList();
        model.addAttribute("data2", totalPage);
        return UsersMapper.toUsersForAdminDTOResponses(listOfActiveUser);

    }

    // 4. get user details

    @Override
    public UsersForAdminDTOResponse getUserDetailsById(int id) {
        Optional<Users> optional = usersRepository.findById(id);
        if (optional.isPresent()) {
            UsersForAdminDTOResponse userDetail = UsersMapper.toUsersForAdminDTOResponse(optional.get());
            return userDetail;
        }
        return null;
    }

    // updating
    @Override
    public void changeActiveStatus(HttpServletResponse response, int id) throws IOException {
        Optional<Users> usersOptional = usersRepository.findById(id);
        if (usersOptional.isPresent()) {
            updateStatus(id);
            Users users = usersOptional.get();
            String data = tableCellHtmlLayout(users);
            response.setContentType("text/html");
            try (PrintWriter out = response.getWriter()) {
                out.println(data);
            }
        }
    }

    // searching
    // 1. search origin
    @Override
    public List<UsersForAdminDTOResponse> searchUsers(Model model, String searchBy, String value, int page) {
        List<Users> groupOfUsers = null;
        int totalPage = getSearchMaxPage(searchBy, value);
        if (page > totalPage)
            page = totalPage;
        if (totalPage == 0)
            page = 1;
        page = page - 1;
        switch (searchBy) {
            case "name":
                groupOfUsers = usersRepository
                        .findByFirstNameContainingOrLastNameContaining(value, value, PageRequest.of(page, PAGE_SIZE))
                        .toList();
                break;
            case "email":
                groupOfUsers = usersRepository.findByEmailContaining(value, PageRequest.of(page, PAGE_SIZE)).toList();
                break;
            case "phone":
                groupOfUsers = usersRepository.findByPhoneContaining(value, PageRequest.of(page, PAGE_SIZE)).toList();
                break;
        }

        model.addAttribute("data2", totalPage);
        return UsersMapper.toUsersForAdminDTOResponses(groupOfUsers);
    }

    // 2. search user lock/nonlock
    @Override
    public List<UsersForAdminDTOResponse> searchInGroupOfLockedUsers(Model model, String searchBy, String value,
            int page,
            Boolean nonLock) {
        Page<Users> groupOfUsers = null;
        boolean isLock = !nonLock;
        int totalPage = getSearchMaxPageLocked(searchBy, value, isLock);
        if (page > totalPage)
            page = totalPage;
        if (totalPage == 0)
            page = 1;
        page = page - 1;
        switch (searchBy) {
            case "name":
                groupOfUsers = usersRepository
                        .findByFirstNameContainingOrLastNameContainingAndNonLocked(value, value, isLock,
                                PageRequest.of(page, PAGE_SIZE));
                break;
            case "email":
                groupOfUsers = usersRepository
                        .findByEmailContainingAndNonLocked(value, isLock, PageRequest.of(page, PAGE_SIZE));
                break;
            case "phone":
                groupOfUsers = usersRepository
                        .findByPhoneContainingAndNonLocked(value, isLock, PageRequest.of(page, PAGE_SIZE));
                break;
        }

        if (groupOfUsers == null) {
            model.addAttribute("data2", 0);
            return UsersMapper.toUsersForAdminDTOResponses(new ArrayList<Users>());
        }
        model.addAttribute("data2", groupOfUsers.getTotalPages());
        return UsersMapper.toUsersForAdminDTOResponses(groupOfUsers.toList());
    }

    // 3. search user active/inactive
    @Override
    public List<UsersForAdminDTOResponse> searchInGroupOfActiveUsers(Model model, String searchBy, String value,
            int page,
            Boolean isActive) {
        Page<Users> groupOfUsers = null;
        isActive = !isActive;
        int totalPage = getSearchMaxPageActive(searchBy, value, isActive);
        if (page > totalPage)
            page = totalPage;
        if (totalPage == 0)
            page = 1;
        page = page - 1;
        switch (searchBy) {
            case "name":
                groupOfUsers = usersRepository.findByFirstNameContainingOrLastNameContainingAndActive(
                        value, value, isActive, PageRequest.of(page, PAGE_SIZE));
                break;
            case "email":
                groupOfUsers = usersRepository.findByEmailContainingAndActive(
                        value, isActive, PageRequest.of(page, PAGE_SIZE));
                break;
            case "phone":
                groupOfUsers = usersRepository.findByPhoneContainingAndActive(
                        value, isActive, PageRequest.of(page, PAGE_SIZE));
                break;
        }

        if (groupOfUsers == null) {
            model.addAttribute("data2", 0);
            return UsersMapper.toUsersForAdminDTOResponses(new ArrayList<Users>());
        }
        model.addAttribute("data2", groupOfUsers.getTotalPages());
        return UsersMapper.toUsersForAdminDTOResponses(groupOfUsers.toList());

    }

    // for update status
    // update
    private void updateStatus(int id) {
        Optional<Users> userOptional = usersRepository.findById(id);
        if (userOptional.isPresent()) {
            Users user = userOptional.get();
            if (user.getRole().equalsIgnoreCase("ROLE_USER")) {
                user.setActive(!user.isActive());
                usersRepository.save(user);
            }
        }
    }

    // html layout
    private String tableCellHtmlLayout(Users users) {
        String data = "<tr  id ='user" + users.getId() + "' class = " + (!users.isActive() ? "'red-row'" : "''") + ">";
        data = data + "<td class='text-center' data-label = 'ID' >" + users.getId() + "</td> \n"
                + "<td  data-label = 'Name' >" + users.getFirstName() + " " + users.getLastName() + "</td> \n"
                + "<td data-label = 'Email' >" + users.getEmail() + "</td>"
                + "<td data-label = 'Phone'  class='admin-center-text'>" + users.getPhone() + "</td> \n";
        data = data + "<td data-label = 'Lock' class='status'> \n";
        if (users.isNonLocked()) {
            data = data + "<button class='positive-status'>Non-lock</button> \n";
        } else {
            data = data + "  <button class='negative-status'>Lock</button> \n";
        }

        data = data + "</td>\n" + "<td data-label = 'Change Status' "
                + "class='status' onclick='changeStatus(" + users.getId() + "," + users.isActive() + ")'> \n";
        data += "<label class='admin_switch'>";
        if (users.isActive()) {
            data = data + "<input type='checkbox' checked disabled> \n";
        } else {
            data = data + "<input type='checkbox' disabled> \n";
        }
        data = data + "<span class='admin_slider admin_round'></span></label></td></tr>";
        data = data +" <td class='admin_user-details' data-label = 'Details'><a href=/admins/user-details/"+users.getId()+" class='btn btn-warning'>";
        data = data + "<i class='fas fa-info-circle'></i>Details</a></td></tr>";
        return data;
    }

    // get total page
    // 1. Origin total page
    private int getMaxPage() {
        int totalRecords = usersRepository.findAll(Pageable.ofSize(PAGE_SIZE)).getTotalPages();
        return totalRecords;
    }

    // 2. Total Page when searching/or all of group active/inactive or
    // locked/nonlock user
    private int getSearchMaxPage(String searchBy, String value) {
        int totalRecords = 0;
        switch (searchBy) {
            case "name":
                totalRecords = usersRepository
                        .findByFirstNameContainingOrLastNameContaining(value, value, Pageable.ofSize(PAGE_SIZE))
                        .getTotalPages();
                break;
            case "email":
                totalRecords = usersRepository.findByEmailContaining(value, Pageable.ofSize(PAGE_SIZE)).getTotalPages();
                break;
            case "phone":
                totalRecords = usersRepository.findByPhoneContaining(value, Pageable.ofSize(PAGE_SIZE)).getTotalPages();
                break;
            case "active":
                boolean isActive = value.contains("true");
                totalRecords = usersRepository.findByActiveAndRole(isActive, "ROLE_USER", Pageable.ofSize(PAGE_SIZE))
                        .getTotalPages();
                break;
            case "locked":
                boolean isLock = value.contains("true");
                totalRecords = usersRepository.findByNonLockedAndRole(isLock, "ROLE_USER", Pageable.ofSize(PAGE_SIZE))
                        .getTotalPages();
                break;
        }

        return totalRecords;
    }

    // 3. Total page when searching in group of active/inactive users
    private int getSearchMaxPageActive(String searchBy, String value, boolean isActive) {
        int totalRecords = 0;
        switch (searchBy) {
            case "name":
                totalRecords = usersRepository
                        .findByFirstNameContainingOrLastNameContainingAndActive(value, value, isActive,
                                Pageable.ofSize(PAGE_SIZE))
                        .getTotalPages();
                break;
            case "email":
                totalRecords = usersRepository
                        .findByEmailContainingAndActive(value, isActive, Pageable.ofSize(PAGE_SIZE)).getTotalPages();
                break;
            case "phone":
                totalRecords = usersRepository
                        .findByPhoneContainingAndActive(value, isActive, Pageable.ofSize(PAGE_SIZE)).getTotalPages();
                break;
        }
        return totalRecords;
    }

    // 4. Total page Total page when searching in group of locked/nonlock users
    private int getSearchMaxPageLocked(String searchBy, String value, boolean isLock) {
        int totalRecords = 0;
        switch (searchBy) {
            case "name":
                totalRecords = usersRepository
                        .findByFirstNameContainingOrLastNameContainingAndNonLocked(value, value, isLock,
                                Pageable.ofSize(PAGE_SIZE))
                        .getTotalPages();
                break;
            case "email":
                totalRecords = usersRepository
                        .findByEmailContainingAndNonLocked(value, isLock, Pageable.ofSize(PAGE_SIZE)).getTotalPages();
                break;
            case "phone":
                totalRecords = usersRepository
                        .findByPhoneContainingAndNonLocked(value, isLock, Pageable.ofSize(PAGE_SIZE)).getTotalPages();
                break;
        }
        return totalRecords;
    }

}
