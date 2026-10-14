package com.dev.babacar.banking.domain.account.exception;

import com.dev.babacar.banking.domain.account.Money;

public class InsufficientBalanceException extends RuntimeException {

    public InsufficientBalanceException(String accountId, Money balance, Money requested) {
        super("Solde insuffisant sur le compte %s : solde = %s, montant demandé = %s"
                .formatted(accountId, balance.amount(), requested.amount()));
    }
}