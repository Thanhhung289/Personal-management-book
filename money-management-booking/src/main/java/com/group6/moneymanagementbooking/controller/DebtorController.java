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
@RequestMapping("/Debtor")
public class DebtorController {
    private final DebtorService debtorService;
    private final UsersService usersService;

    @GetMapping("/ListAll")
    public String listDebtor(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "6") int pageSize,
            Model model, HttpServletRequest request, @ModelAttribute("errorMessage") String errorMessage) {
        Pageable pageable = PaginationUtil.getPageable(page, pageSize);
        // List<Debtor> items = debtorService.findAll(getIdUser());
        Page<Debtor> itemsPage = PaginationUtil.paginate(pageable, debtorService.findAll(getIdUser()));
        String currentRequestMapping = request.getRequestURI();

        // Debtor debtor = new Debtor();
        // debtor.setUserId(getIdUser());
        // // model.addAttribute("isUpdate", false);
        // model.addAttribute("debtor", debtor);
        // model.addAttribute("title", "Add");

        // model.addAttribute("mess", errorMessage);
        // model.addAttribute("page", itemsPage);
        // model.addAttribute("link", currentRequestMapping);
        return dispathcher(model, errorMessage, itemsPage, currentRequestMapping);
    }

    @GetMapping("/ListDebtor")
    public String AllDebtor(Model model, @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int pageSize, HttpServletRequest request) {
        List<Debtor> listdeb = debtorService.getListDebtor();
        Pageable pageable = PaginationUtil.getPageable(page, pageSize);
        Page<Debtor> itemsPage = PaginationUtil.paginate(pageable, listdeb);
        String currentRequestMapping = request.getRequestURI();
        // model.addAttribute("page", itemsPage);
        // model.addAttribute("link", currentRequestMapping);
        return dispathcher(model, "", itemsPage, currentRequestMapping);
    }

    @GetMapping("/ListOwner")
    public String AllOwner(Model model, @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int pageSize, HttpServletRequest request) {
        List<Debtor> listdeb = debtorService.getListOwner();
        Pageable pageable = PaginationUtil.getPageable(page, pageSize);
        Page<Debtor> itemsPage = PaginationUtil.paginate(pageable, listdeb);
        String currentRequestMapping = request.getRequestURI();

        // Debtor debtor = new Debtor();
        // debtor.setUserId(getIdUser());
        // // model.addAttribute("isUpdate", false);
        // model.addAttribute("debtor", debtor);
        // model.addAttribute("title", "Add");

        // model.addAttribute("page", itemsPage);
        // model.addAttribute("link", currentRequestMapping);
        return dispathcher(model, "", itemsPage, currentRequestMapping);
    }

    // @GetMapping("/Add")
    // public String addNew(Model model, HttpServletRequest request) {
    // Debtor debtor = new Debtor();
    // debtor.setUserId(getIdUser());
    // model.addAttribute("isUpdate", false);
    // model.addAttribute("debtor", debtor);
    // model.addAttribute("title", "Add");
    // return "add-debtor";
    // }

    @PostMapping("/Add")
    public String addNew(@ModelAttribute("debtor") Debtor debtor) throws Exception {
        debtorService.Save(debtor);
        return "redirect:/Debtor/ListAll";
    }

    @GetMapping("/Search")
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

    @GetMapping("/Filter")
    public String searchDebtor(Model model,
            @RequestParam(value = "nameDebtor", required = false) String name,
            @RequestParam(value = "filterType", required = false) String filterType,
            @RequestParam(value = "filterValueStart", required = false) String filterValueStart,
            @RequestParam(value = "filterValueEnd", required = false) String filterValueEnd,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int pageSize,
            HttpServletRequest request) throws Exception {

        String currentRequestMapping = request.getRequestURI();
        List<Debtor> items = debtorService.FilterDebtor(filterType, name, filterValueStart, filterValueEnd);
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

    // @GetMapping("/edit/{id}")
    // public String registerGet(Model model, @PathVariable("id") int id) {
    // Optional<Debtor> debtor = debtorService.getDebtor(id);
    // // model.addAttribute("isUpdate", true);
    // model.addAttribute("debtor", debtor.get());
    // model.addAttribute("title", "Update");
    // return "add-debtor";
    // }

    @PostMapping("/update")
    public String updateDebtor(Model model, @ModelAttribute("debtor") Debtor debtor) throws Exception {
        debtorService.Update(debtor);
        debtorService.Update(debtor);
        return "redirect:/Debtor/ListAll";
    }

    @GetMapping("/delete/{id}")

    public String deleteDebtor(Model model, @PathVariable("id") int id, RedirectAttributes redirectAttributes) {
        Debtor debtor = (debtorService.getDebtor(id)).get();
        if (debtor.getTotal() != 0) {
            model.addAttribute("list_debtor", debtorService.findAll(getIdUser()));
            model.addAttribute("record", debtorService.findAll(getIdUser()).size());
            redirectAttributes.addAttribute("report",
                    "Warning: Cannot be delete because the debt has not been completed!");
            return "redirect:/Debtor/ListAll";
        }
        debtorService.deleteDebtorById(id);
        return "redirect:/Debtor/ListAll";
    }

    private int getIdUser() {
        Users users = usersService.getUserByEmail(SecurityUtils.getCurrentUsername());
        return users.getId();
    }

    private String dispathcher(Model model, String errorMessage, Page<Debtor> itemsPage,
            String currentRequestMapping) {
        Debtor debtor = new Debtor();
        debtor.setUserId(getIdUser());
        model.addAttribute("debtor", debtor);

        model.addAttribute("mess", errorMessage);
        model.addAttribute("page", itemsPage);
        model.addAttribute("link", currentRequestMapping);
        return "view-debtor";
    }

}
