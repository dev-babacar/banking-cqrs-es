package com.dev.babacar.banking.domain.command;

import com.dev.babacar.banking.domain.account.Money;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

public record OpenAccountCommand(
        @TargetAggregateIdentifier String accountId,
        String ownerName,
        Money initialBalance
) {
}