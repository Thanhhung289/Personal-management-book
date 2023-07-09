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
import org.springframework.web.servlet.view.RedirectView;

import com.group6.moneymanagementbooking.enity.Accounts;
import com.group6.moneymanagementbooking.enity.Debt_detail;
import com.group6.moneymanagementbooking.enity.Users;
import com.group6.moneymanagementbooking.service.AccountsService;
import com.group6.moneymanagementbooking.service.DebtorService;
import com.group6.moneymanagementbooking.service.DetailDebtService;
import com.group6.moneymanagementbooking.service.UsersService;
import com.group6.moneymanagementbooking.util.PaginationUtil;
import com.group6.moneymanagementbooking.util.SecurityUtils;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@CrossOrigin
@RequestMapping("/Debtor/Detail")
public class DetailDebtController {
    private final DetailDebtService detailDebtService;
    private final DebtorService debtorService;
    private final UsersService usersService;
    private final AccountsService accountsService;

    private int idDebtor;

    @GetMapping("/view-detail/{id}")
    public String listDetailDebt(Model model, @PathVariable("id") int id, @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int pageSize, HttpServletRequest request,
            @ModelAttribute("errorMessage") String errorMessage) {
        idDebtor = id;
        Pageable pageable = PaginationUtil.getPageable(page, pageSize);
        List<Debt_detail> items = detailDebtService.findAllById(id);
        Page<Debt_detail> itemsPage = PaginationUtil.paginate(pageable, items);
        String currentRequestMapping = request.getRequestURI();

        Debt_detail debt_detail = new Debt_detail();
        debt_detail.setDeptorId(id);
        model.addAttribute("errorMessage", errorMessage);
        model.addAttribute("listAccount", accountsService.findAllByUserId(getIdUser()));
        model.addAttribute("debt_detail", debt_detail);

        model.addAttribute("listAcc", accountsService.findAllByUserId(getIdUser()).size());
        model.addAttribute("debtor", debtorService.getDebtorById(id));
        model.addAttribute("page", itemsPage);
        model.addAttribute("link", currentRequestMapping);
        return "view-debt";
        // return dispathcher(model, debt_detail,
        // accountsService.findAllByUserId(getIdUser()).size(), errorMessage,
        // itemsPage, currentRequestMapping, "", "", "");
    }

    // @GetMapping("/Add/{id}")
    // public String registerGet(Model model, @PathVariable("id") int id,
    // @ModelAttribute("errorMessage") String errorMessage) {
    // Debt_detail debt_detail = new Debt_detail();
    // debt_detail.setDeptorId(id);
    // model.addAttribute("errorMessage", errorMessage);
    // model.addAttribute("listAccount",
    // accountsService.findAllByUserId(getIdUser()));
    // model.addAttribute("debt_detail", debt_detail);
    // model.addAttribute("title", "Add New");
    // return "add-detail-debt";
    // }

    @PostMapping("/Add")
    public RedirectView addDetailDebt(Model model, @ModelAttribute("debt_detail") Debt_detail detail_edbt,
            HttpServletRequest request, RedirectAttributes redirectAttributes)
            throws Exception {

        Accounts acc = accountsService.findById(detail_edbt.getAccounts().getId());
        if (!acc.isActive()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Your account is inactive!");
            RedirectView redirectView = new RedirectView();
            redirectView.setUrl("/Debtor/Detail/view-detail/" + detail_edbt.getDeptorId());
            return redirectView;
        }
        if ((detail_edbt.isClassify() && (detail_edbt.getMoney_debt() > acc.getBalance()))) {
            // RedirectAttributes redirectAttributes ;
            redirectAttributes.addFlashAttribute("errorMessage", "The amount you entered exceeds the account balance!");
            RedirectView redirectView = new RedirectView();
            redirectView.setUrl("/Debtor/Detail/view-detail/" + detail_edbt.getDeptorId());
            return redirectView;
        }

        detailDebtService.Save(detail_edbt);
        RedirectView redirectView = new RedirectView();
        redirectView.setUrl("/Debtor/Detail/view-detail/" + detail_edbt.getDeptorId());
        return redirectView;
    }

    @GetMapping("/Delete/{id}")
    public RedirectView deleteDeatil(Model model, @PathVariable("id") int id) {
        Debt_detail deb = detailDebtService.findById(id);
        detailDebtService.deleteDebtById(id);
        RedirectView redirectView = new RedirectView();
        redirectView.setUrl("/Debtor/Detail/view-detail/" + deb.getDeptorId());
        return redirectView;
    }

    @PostMapping("/Update/{id}")
    public RedirectView UpdateDebt(Model model, @PathVariable("id") int id,
            @ModelAttribute("debtor") Debt_detail newdebt_detail) {
        Debt_detail deb = detailDebtService.findById(id);
        newdebt_detail.setId(deb.getId());
        detailDebtService.UpdateDebt(deb, newdebt_detail);
        RedirectView redirectView = new RedirectView();
        redirectView.setUrl("/Debtor/Detail/view-detail/" + deb.getDeptorId());
        return redirectView;
    }

    @GetMapping("/Details/{id}")
    public String editDebt(Model model, @PathVariable("id") int id) {
        Debt_detail deb = detailDebtService.findById(id);
        model.addAttribute("debtor", debtorService.getDebtorById(deb.getDeptorId()));
        model.addAttribute("debt_detail", deb);
        model.addAttribute("title", "Edit");
        return "view-detaildebt";
    }

    @GetMapping("/Filter")
    public String searchDebtor(Model model,
            @RequestParam(value = "filterType", required = false) String filterType,
            @RequestParam(value = "filterValueStart", required = false) String filterValueStart,
            @RequestParam(value = "filterValueEnd", required = false) String filterValueEnd,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int pageSize,
            HttpServletRequest request) throws Exception {

        String currentRequestMapping = request.getRequestURI();
        List<Debt_detail> items = detailDebtService.FilterDebtor(idDebtor, filterType, filterValueStart,
                filterValueEnd);
        Pageable pageable = PaginationUtil.getPageable(page, pageSize);
        Page<Debt_detail> itemsPage = PaginationUtil.paginate(pageable, items);

        Debt_detail debt_detail = new Debt_detail();
        debt_detail.setDeptorId(idDebtor);
        model.addAttribute("errorMessage", "");
        model.addAttribute("listAccount",
                accountsService.findAllByUserId(getIdUser()));
        model.addAttribute("debt_detail", debt_detail);
        model.addAttribute("listAcc", accountsService.findAllByUserId(getIdUser()).size());
        model.addAttribute("debtor", debtorService.getDebtorById(idDebtor));
        model.addAttribute("filterType", filterType);
        model.addAttribute("filterValueStart", filterValueStart);
        model.addAttribute("filterValueEnd", filterValueEnd);
        model.addAttribute("page", itemsPage);
        model.addAttribute("link", currentRequestMapping);
        // return dispathcher(model, debt_detail, 0, "", itemsPage,
        // currentRequestMapping, filterType, filterValueStart,
        // filterValueEnd);
        return "view-debt";

    }

    private int getIdUser() {
        Users users = usersService.getUserByEmail(SecurityUtils.getCurrentUsername());
        return users.getId();
    }

    // private String dispathcher(Model model, Debt_detail debt_detail, int listAcc,
    // String errorMessage, Page<Debt_detail> itemsPage,
    // String currentRequestMapping, String filterType, String filterValueStart,
    // String filterValueEnd) {

    // model.addAttribute("errorMessage", "");
    // model.addAttribute("listAccount",
    // accountsService.findAllByUserId(getIdUser()));
    // model.addAttribute("debt_detail", debt_detail);

    // model.addAttribute("debtor", debtorService.getDebtorById(idDebtor));
    // model.addAttribute("filterType", filterType);
    // model.addAttribute("filterValueStart", filterValueStart);
    // model.addAttribute("filterValueEnd", filterValueEnd);
    // model.addAttribute("page", itemsPage);
    // model.addAttribute("link", currentRequestMapping);
    // return "view-debt";

    // }
}
