package com.dev.babacar.banking.domain.event;

import com.dev.babacar.banking.domain.account.Money;

public record MoneyWithdrawnEvent(String accountId, Money amount) {
}