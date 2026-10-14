package com.dev.babacar.banking.domain.event;

import com.dev.babacar.banking.domain.account.Money;

public record AccountOpenedEvent(
        String accountId,
        String ownerName,
        Money initialBalance
) {
}