package com.group6.moneymanagementbooking.service.impl;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import com.group6.moneymanagementbooking.enity.Accounts;
import com.group6.moneymanagementbooking.repository.AccountsRepository;
import com.group6.moneymanagementbooking.repository.UsersRepository;
import com.group6.moneymanagementbooking.service.AccountsService;
import com.group6.moneymanagementbooking.util.SecurityUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AccountsServiceImpl implements AccountsService {
    @Autowired
    private final AccountsRepository accountsRepository;
    private final UsersRepository usersRepository;

    @Override
    public Accounts addAccounts(Accounts accounts, Model model) throws Exception {
        try {
            accounts.setUserId(usersRepository.findByEmail(SecurityUtils.getCurrentUsername()).get().getId());
            boolean checkAccountExits = accountsRepository
                    .findByNameAndUser_id(accounts.getName(), accounts.getUserId()).isPresent();
            if (checkAccountExits)
                throw new Exception("Warning: Account name already exists");
            if (accounts.getBalance() < 0)
                throw new Exception("Warning: Balance must be greater than 0");
            if (accounts.getName().isEmpty())
                throw new Exception("Warning: Name cannot be null");
            return accountsRepository.save(accounts);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    @Override
    public List<Accounts> findAll() {
        return accountsRepository
                .findAllByUserId(usersRepository.findByEmail(SecurityUtils.getCurrentUsername()).get().getId());
    }

    @Override
    public void updateActiveById(boolean action, int id) {
        accountsRepository.updateActiveById(action, id);
    }

    @Override
    public void addBalance(double balance, int id) {
        accountsRepository.addBalanceById(balance + accountsRepository.findBalanceById(id), id);
    }

    @Override
    public void expenseBalance(double balance, int id) {
        accountsRepository.expenseBalanceById(accountsRepository.findBalanceById(id) - balance, id);
    }

    @Override
    public Optional<Accounts> findOptinalById(int id) {
        return accountsRepository.findById(id);
    }

    @Override
    public List<Accounts> findByActive() {
        return accountsRepository.findByActiveAndUserId(true,
                usersRepository.findByEmail(SecurityUtils.getCurrentUsername()).get().getId());
    }

    @Override
    public Accounts updateAccount(Accounts accounts) {
        accounts.setUserId(usersRepository.findByEmail(SecurityUtils.getCurrentUsername()).get().getId());
        return accountsRepository.save(accounts);
    }

    @Override
    public void deleteById(int id) {
        accountsRepository.deleteById(id);
    }

    @Override
    public List<Accounts> findAllByUserId(int userId) {
        return accountsRepository.findAllByUserId(userId);
    }

    @Override
    public Accounts findById(int id) {
        return (accountsRepository.findById(id)).get();
    }

    @Override
    public void changeActiveStatus(HttpServletResponse response, int id) throws IOException {
        Optional<Accounts> accountsOptional = accountsRepository.findById(id);
        if (accountsOptional.isPresent()) {
            updateStatus(id);
        }
    }

    private void updateStatus(int id) {
        Optional<Accounts> accOptional = accountsRepository.findById(id);
        if (accOptional.isPresent()) {
            Accounts accounts = accOptional.get();
            accounts.setActive(!accounts.isActive());
            accountsRepository.save(accounts);
        }
    }

    @Override
    public Map<String, Integer> getTransactionCount(Collection<Accounts> accounts) {
        Map<String, Integer> transactionCountMap = new HashMap<>();
        for (Accounts account : accounts) {
            int transactionCount = account.getExpenses().size() + account.getIncome().size();
            transactionCountMap.put(account.getName(), transactionCount);
        }
        return transactionCountMap;
    }

    @Override
    public double getTotalIncome() {
        return findAllByUserId(usersRepository.findByEmail(SecurityUtils.getCurrentUsername()).get().getId()).stream()
                .mapToDouble(Accounts::getTotalIncome).sum();
    }

    @Override
    public double getTotalExpenses() {
        return findAllByUserId(usersRepository.findByEmail(SecurityUtils.getCurrentUsername()).get().getId()).stream()
                .mapToDouble(Accounts::getTotalExpenses).sum();
    }

    @Override
    public double getTotalBalance() {
        double sum = 0;
        List<Accounts> list = findAllByUserId(
                usersRepository.findByEmail(SecurityUtils.getCurrentUsername()).get().getId());
        for (Accounts i : list) {
            sum += i.getBalance();
        }
        return sum;
    }

    @Override
    public Iterable<Accounts> getAllAccounts() {
        return findAllByUserId(usersRepository.findByEmail(SecurityUtils.getCurrentUsername()).get().getId());
    }

}
