package com.dev.babacar.banking.domain.account;

import com.dev.babacar.banking.domain.command.OpenAccountCommand;
import com.dev.babacar.banking.domain.event.AccountOpenedEvent;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;

import java.time.Instant;

@Aggregate
public class BankAccount {

    @AggregateIdentifier
    private String accountId;

    private String ownerName;
    private Money balance;

    protected BankAccount() {
    }

    @CommandHandler
    public BankAccount(OpenAccountCommand command) {
        if (command.initialBalance().isNegative()) {
            throw new IllegalArgumentException(
                    "Le solde initial ne peut pas être négatif");
        }
        if (command.ownerName() == null || command.ownerName().isBlank()) {
            throw new IllegalArgumentException(
                    "Le nom du titulaire est obligatoire");
        }

        AggregateLifecycle.apply(new AccountOpenedEvent(
                command.accountId(),
                command.ownerName(),
                command.initialBalance(),
                Instant.now()
        ));
    }

    @EventSourcingHandler
    public void on(AccountOpenedEvent event) {
        this.accountId = event.accountId();
        this.ownerName = event.ownerName();
        this.balance = event.initialBalance();
    }
}