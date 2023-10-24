package com.group6.moneymanagementbooking.service;

import java.util.List;
import java.util.Optional;

import com.group6.moneymanagementbooking.enity.Debtor;

public interface DebtorService {

    public List<Debtor> findAll(int id);

    public boolean Save(Debtor debtor);

    public Debtor Update(Debtor debtor);

    public List<Debtor> SearchByName(String name);

    public Optional<Debtor> getDebtor(int id);

    public void deleteDebtorById(int id);

    public Debtor getDebtorById(int id);

    public List<Debtor> getListDebtor();

    public List<Debtor> getListOwner();

    public List<Debtor> FilterDebtor(String filterType, String filterValueStart, String filterValueEnd);

}
