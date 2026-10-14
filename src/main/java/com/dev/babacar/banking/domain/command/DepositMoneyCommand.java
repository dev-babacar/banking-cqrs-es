package com.dev.babacar.banking.domain.command;

import com.dev.babacar.banking.domain.account.Money;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

public record DepositMoneyCommand(
        @TargetAggregateIdentifier String accountId,
        Money amount
) {
}