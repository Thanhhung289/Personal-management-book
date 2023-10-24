package com.group6.moneymanagementbooking.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.group6.moneymanagementbooking.enity.Debtor;
import com.group6.moneymanagementbooking.enity.Users;
import com.group6.moneymanagementbooking.exception.SystemBadRequestException;
import com.group6.moneymanagementbooking.repository.DebtorRepository;
import com.group6.moneymanagementbooking.service.DebtorService;
import com.group6.moneymanagementbooking.service.UsersService;
import com.group6.moneymanagementbooking.util.SecurityUtils;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor

public class DebtorServiceImpl implements DebtorService {

  private final DebtorRepository debtorRepository;
  private final UsersService usersService;

  @Override
  public List<Debtor> findAll(int id) {
    List<Debtor> debtorList = debtorRepository.findAllByUserid(id);
    Collections.sort(debtorList, new Comparator<Debtor>() {
      @Override
      public int compare(Debtor debtor1, Debtor debtor2) {
        // Sắp xếp giảm dần theo debtor id
        return Integer.compare(debtor2.getId(), debtor1.getId());
      }
    });
    return debtorList;
  }

  @Override
  public boolean Save(Debtor debtor) {
    List<Debtor> listDebt = debtorRepository.findAll();
    boolean valid = true;

    for (Debtor debtor2 : listDebt) {
      if ((debtor.getEmail().equals(debtor2.getEmail())) || (debtor.getPhone().equals(debtor2.getPhone()))) {
        valid = false;
        break;
      }
    }

    if (valid == true) {
      debtor.setTotal(0.0);
      debtor.setDate_create(LocalDateTime.now());
      debtor.setDate_update(LocalDateTime.now());
      debtorRepository.save(debtor);
    }

    return valid;
  }

  @Override
  public List<Debtor> SearchByName(String name) {
    return debtorRepository.findAllByNameContainingAnduserid(getIdUser(), name);
  }

  @Override
  public Optional<Debtor> getDebtor(int id) {
    return debtorRepository.findById(id);
  }

  @Override
  public void deleteDebtorById(int id) {
    debtorRepository.deleteById(id);
  }

  @Override
  public Debtor getDebtorById(int id) {
    return (debtorRepository.findById(id)).get();
  }

  @Override
  public Debtor Update(Debtor debtor) {
    Optional<Debtor> debtors = getDebtor(debtor.getId());
    debtor.setDate_create(debtors.get().getDate_create());
    debtor.setDate_update(LocalDateTime.now());
    debtor.setTotal(debtors.get().getTotal());
    return debtorRepository.save(debtor);
  }

  @Override
  public List<Debtor> getListOwner() {
    List<Debtor> listdeb = new ArrayList<>();
    for (var deb : findAll(getIdUser())) {
      if (deb.getTotal() < 0) {
        listdeb.add(deb);
      }
    }
    return listdeb;
  }

  @Override
  public List<Debtor> getListDebtor() {
    List<Debtor> listdeb = new ArrayList<>();
    for (var deb : findAll(getIdUser())) {
      if (deb.getTotal() > 0) {
        listdeb.add(deb);
      }
    }
    return listdeb;
  }

  @Override
  public List<Debtor> FilterDebtor(String filterType, String filterValueStart, String filterValueEnd) {
    List<Debtor> listdebtor = new ArrayList<>();
    // if (name == null) {
    // name = "";
    // }

    if ("total".equals(filterType)) {
      if (filterValueStart != "" && filterValueEnd != "") {
        try {
          Double from = Double.parseDouble(filterValueStart.trim());
          Double to = Double.parseDouble(filterValueEnd.trim());
          listdebtor = debtorRepository.findAllByTotal(getIdUser(), from, to);
        } catch (NumberFormatException e) {
          e.printStackTrace();
        }
      } else if (filterValueStart == "" && filterValueEnd == "") {
        listdebtor = findAll(getIdUser());
      }
    } else if ("date".equals(filterType)) {
      if (filterValueStart != "" && filterValueEnd != "") {
        try {
          String pattern = "yyyy-MM-dd";
          DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(pattern);
          LocalDate dateStart = LocalDate.parse(filterValueStart.trim(), dateFormatter);
          LocalDate dateEnd = LocalDate.parse(filterValueEnd.trim(), dateFormatter);
          LocalDateTime dateTimeStart = dateStart.atStartOfDay();
          LocalDateTime dateTimeEnd = dateEnd.atStartOfDay();
          listdebtor = debtorRepository.findAllByDate(getIdUser(), dateTimeStart, dateTimeEnd);
        } catch (DateTimeParseException e) {
          e.printStackTrace();
        }
      } else if (filterValueStart == "" && filterValueEnd == "") {
        listdebtor = findAll(getIdUser());
      }
    }

    return listdebtor;
  }

  private int getIdUser() {
    Users users = usersService.getUserByEmail(SecurityUtils.getCurrentUsername());
    return users.getId();
  }

}
