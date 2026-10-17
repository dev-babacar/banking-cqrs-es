package com.dev.babacar.banking.application.service;

import com.dev.babacar.banking.application.port.in.DepositMoneyUseCase;
import com.dev.babacar.banking.application.port.in.OpenAccountUseCase;
import com.dev.babacar.banking.application.port.in.WithdrawMoneyUseCase;
import com.dev.babacar.banking.domain.account.Money;
import com.dev.babacar.banking.domain.command.DepositMoneyCommand;
import com.dev.babacar.banking.domain.command.OpenAccountCommand;
import com.dev.babacar.banking.domain.command.WithdrawMoneyCommand;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AccountApplicationService
        implements OpenAccountUseCase, DepositMoneyUseCase, WithdrawMoneyUseCase {

    private final CommandGateway commandGateway;

    public AccountApplicationService(CommandGateway commandGateway) {
        this.commandGateway = commandGateway;
    }

    @Override
    public String openAccount(String ownerName, Money initialBalance) {
        String accountId = UUID.randomUUID().toString();
        commandGateway.sendAndWait(new OpenAccountCommand(accountId, ownerName, initialBalance));
        return accountId;
    }

    @Override
    public void deposit(String accountId, Money amount) {
        commandGateway.sendAndWait(new DepositMoneyCommand(accountId, amount));
    }

    @Override
    public void withdraw(String accountId, Money amount) {
        commandGateway.sendAndWait(new WithdrawMoneyCommand(accountId, amount));
    }
}