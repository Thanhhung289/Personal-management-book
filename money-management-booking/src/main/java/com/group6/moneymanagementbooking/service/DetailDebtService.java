package com.group6.moneymanagementbooking.service;

import java.util.List;

import com.group6.moneymanagementbooking.enity.Debt_detail;

public interface DetailDebtService {

    public List<Debt_detail> findAll();

    public List<Debt_detail> findAllById(int id);

    public Debt_detail Save(Debt_detail detail_edbt);

    public void deleteDebtById(int id);

    public Debt_detail findById(int id);

    // public List<Debt_detail> getDetailDebt(int id);

}
