package com.group6.moneymanagementbooking.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.group6.moneymanagementbooking.enity.Debtor;
import com.group6.moneymanagementbooking.enity.Users;
import com.group6.moneymanagementbooking.service.DebtorService;
import com.group6.moneymanagementbooking.service.UsersService;
import com.group6.moneymanagementbooking.util.PaginationUtil;
import com.group6.moneymanagementbooking.util.SecurityUtils;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@CrossOrigin
@RequestMapping("/debtor")
public class DebtorController {
    private final DebtorService debtorService;
    private final UsersService usersService;

    @GetMapping("/list-all")
    public String listDebtor(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "6") int pageSize,
            Model model, HttpServletRequest request) {
        Pageable pageable = PaginationUtil.getPageable(page, pageSize);
        Page<Debtor> itemsPage = PaginationUtil.paginate(pageable, debtorService.findAll(getIdUser()));
        String currentRequestMapping = request.getRequestURI();
        return dispathcher(model, itemsPage, currentRequestMapping);
    }

    @GetMapping("/list-debtor")
    public String AllDebtor(Model model, @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int pageSize, HttpServletRequest request) {
        List<Debtor> listdeb = debtorService.getListDebtor();
        Pageable pageable = PaginationUtil.getPageable(page, pageSize);
        Page<Debtor> itemsPage = PaginationUtil.paginate(pageable, listdeb);
        String currentRequestMapping = request.getRequestURI();
        return dispathcher(model, itemsPage, currentRequestMapping);
    }

    @GetMapping("/list-owner")
    public String AllOwner(Model model, @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int pageSize, HttpServletRequest request) {
        List<Debtor> listdeb = debtorService.getListOwner();
        Pageable pageable = PaginationUtil.getPageable(page, pageSize);
        Page<Debtor> itemsPage = PaginationUtil.paginate(pageable, listdeb);
        String currentRequestMapping = request.getRequestURI();
        return dispathcher(model, itemsPage, currentRequestMapping);
    }

    @PostMapping("/add")
    public String addNew(@ModelAttribute("debtor") Debtor debtor, RedirectAttributes redirectAttributes)
            throws Exception {
        if (!debtorService.Save(debtor)) {
            redirectAttributes.addAttribute("report",
                    "Email or phone number is existing. Save failed!");
        } else {
            redirectAttributes.addAttribute("report", "Save successfully!");
        }
        return "redirect:/debtor/list-all";
    }

    @GetMapping("/search")
    public String searchDebtor(Model model, @RequestParam(value = "nameDebtor", required = false) String name,
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "6") int pageSize,
            HttpServletRequest request)
            throws Exception {

        String currentRequestMapping = request.getRequestURI();
        List<Debtor> items = debtorService.SearchByName(name);
        Pageable pageable = PaginationUtil.getPageable(page, pageSize);
        Page<Debtor> itemsPage = PaginationUtil.paginate(pageable, items);
        Debtor debtor = new Debtor();
        debtor.setUserId(getIdUser());

        model.addAttribute("debtor", debtor);
        model.addAttribute("nameDebtor", name);
        model.addAttribute("page", itemsPage);
        model.addAttribute("link", currentRequestMapping);
        return "view-debtor";
    }

    @GetMapping("/filter")
    public String searchDebtor(Model model,
            @RequestParam(value = "filterType", required = false) String filterType,
            @RequestParam(value = "filterValueStart", required = false) String filterValueStart,
            @RequestParam(value = "filterValueEnd", required = false) String filterValueEnd,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int pageSize,
            HttpServletRequest request) throws Exception {

        String currentRequestMapping = request.getRequestURI();
        List<Debtor> items = debtorService.FilterDebtor(filterType, filterValueStart, filterValueEnd);
        Pageable pageable = PaginationUtil.getPageable(page, pageSize);
        Page<Debtor> itemsPage = PaginationUtil.paginate(pageable, items);
        Debtor debtor = new Debtor();
        debtor.setUserId(getIdUser());

        model.addAttribute("debtor", debtor);
        model.addAttribute("filterType", filterType);
        model.addAttribute("filterValueStart", filterValueStart);
        model.addAttribute("filterValueEnd", filterValueEnd);
        model.addAttribute("page", itemsPage);
        model.addAttribute("link", currentRequestMapping);

        return "view-debtor";
    }

    @PostMapping("/update")
    public String updateDebtor(Model model, @ModelAttribute("debtor") Debtor debtor) throws Exception {
        debtorService.Update(debtor);
        debtorService.Update(debtor);
        return "redirect:/debtor/list-all";
    }

    @GetMapping("/delete/{id}")
    public String deleteDebtor(Model model, @PathVariable("id") int id, RedirectAttributes redirectAttributes) {
        Debtor debtor = (debtorService.getDebtor(id)).get();
        if (debtor.getTotal() != 0) {
            model.addAttribute("list_debtor", debtorService.findAll(getIdUser()));
            model.addAttribute("record", debtorService.findAll(getIdUser()).size());
            redirectAttributes.addAttribute("report",
                    "Warning: Cannot be delete because the debt has not been completed!");
            return "redirect:/debtor/list-all";
        }
        debtorService.deleteDebtorById(id);
        return "redirect:/debtor/list-all";
    }

    private int getIdUser() {
        Users users = usersService.getUserByEmail(SecurityUtils.getCurrentUsername());
        return users.getId();
    }

    private String dispathcher(Model model, Page<Debtor> itemsPage,
            String currentRequestMapping) {
        Debtor debtor = new Debtor();
        debtor.setUserId(getIdUser());

        model.addAttribute("debtor", debtor);
        model.addAttribute("page", itemsPage);
        model.addAttribute("link", currentRequestMapping);
        return "view-debtor";
    }

}
