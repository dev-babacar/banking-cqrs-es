package com.dev.babacar.banking.domain.command;

import com.dev.babacar.banking.domain.account.Money;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

public record WithdrawMoneyCommand(
        @TargetAggregateIdentifier String accountId,
        Money amount
) {
}