package com.dev.babacar.banking.domain.event;

import com.dev.babacar.banking.domain.account.Money;
import java.time.Instant;

public record AccountOpenedEvent(
        String accountId,
        String ownerName,
        Money initialBalance,
        Instant openedAt
) {
}