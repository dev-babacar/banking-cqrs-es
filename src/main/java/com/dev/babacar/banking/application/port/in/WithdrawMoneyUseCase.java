package com.dev.babacar.banking.application.port.in;

import com.dev.babacar.banking.domain.account.Money;

public interface WithdrawMoneyUseCase {

    void withdraw(String accountId, Money amount);
}