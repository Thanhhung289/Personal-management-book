package com.group6.moneymanagementbooking.enity;

import java.util.Collection;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "accounts")
public class Accounts {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "user_id")
    private int userId;

    private String name;

    private double balance;

    @Column(name = "is_active")
    private boolean active;

    @OneToMany(mappedBy = "accounts", cascade = CascadeType.ALL)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Collection<Expenses> expenses;

    @OneToMany(mappedBy = "accounts", cascade = CascadeType.ALL)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Collection<Income> income;

    @OneToMany(mappedBy = "accounts", cascade = CascadeType.ALL)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Collection<Debt_detail> debtDetail;

    public double getTotalIncome() {
        if (income != null && !income.isEmpty()) {
            return income.stream().mapToDouble(Income::getAmount).sum();
        }
        return 0.0;
    }

    public double getTotalExpenses() {
        if (expenses != null && !expenses.isEmpty()) {
            return expenses.stream().mapToDouble(Expenses::getAmount).sum();
        }
        return 0.0;
    }

}
