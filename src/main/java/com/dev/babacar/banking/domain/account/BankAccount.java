package com.dev.babacar.banking.domain.account;

import com.dev.babacar.banking.domain.account.exception.InsufficientBalanceException;
import com.dev.babacar.banking.domain.command.DepositMoneyCommand;
import com.dev.babacar.banking.domain.command.OpenAccountCommand;
import com.dev.babacar.banking.domain.command.WithdrawMoneyCommand;
import com.dev.babacar.banking.domain.event.AccountOpenedEvent;
import com.dev.babacar.banking.domain.event.MoneyDepositedEvent;
import com.dev.babacar.banking.domain.event.MoneyWithdrawnEvent;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;

@Aggregate
public class BankAccount {

    @AggregateIdentifier
    private String accountId;

    private String ownerName;
    private Money balance;

    // Constructeur vide obligatoire : Axon l'utilise avant de rejouer les événements.
    protected BankAccount() {
    }

    // ---------- Commandes (valident, ne modifient jamais l'état) ----------

    @CommandHandler
    public BankAccount(OpenAccountCommand command) {
        if (command.initialBalance().isNegative()) {
            throw new IllegalArgumentException("Le solde initial ne peut pas être négatif");
        }
        if (command.ownerName() == null || command.ownerName().isBlank()) {
            throw new IllegalArgumentException("Le nom du titulaire est obligatoire");
        }

        AggregateLifecycle.apply(new AccountOpenedEvent(
                command.accountId(),
                command.ownerName(),
                command.initialBalance()
        ));
    }

    @CommandHandler
    public void handle(DepositMoneyCommand command) {
        requirePositive(command.amount());

        AggregateLifecycle.apply(new MoneyDepositedEvent(accountId, command.amount()));
    }

    @CommandHandler
    public void handle(WithdrawMoneyCommand command) {
        requirePositive(command.amount());

        if (!balance.isGreaterThanOrEqualTo(command.amount())) {
            throw new InsufficientBalanceException(accountId, balance, command.amount());
        }

        AggregateLifecycle.apply(new MoneyWithdrawnEvent(accountId, command.amount()));
    }

    // ---------- Événements (modifient l'état, ne valident jamais) ----------

    @EventSourcingHandler
    public void on(AccountOpenedEvent event) {
        this.accountId = event.accountId();
        this.ownerName = event.ownerName();
        this.balance = event.initialBalance();
    }

    @EventSourcingHandler
    public void on(MoneyDepositedEvent event) {
        this.balance = this.balance.add(event.amount());
    }

    @EventSourcingHandler
    public void on(MoneyWithdrawnEvent event) {
        this.balance = this.balance.subtract(event.amount());
    }

    private void requirePositive(Money amount) {
        if (amount == null || !amount.isPositive()) {
            throw new IllegalArgumentException("Le montant doit être strictement positif");
        }
    }
}