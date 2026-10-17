package com.dev.babacar.banking.application.port.in;

import com.dev.babacar.banking.domain.account.Money;

public interface DepositMoneyUseCase {

    void deposit(String accountId, Money amount);
}