package com.dev.babacar.banking.domain.account;

import com.dev.babacar.banking.domain.account.exception.InsufficientBalanceException;
import com.dev.babacar.banking.domain.command.DepositMoneyCommand;
import com.dev.babacar.banking.domain.command.OpenAccountCommand;
import com.dev.babacar.banking.domain.command.WithdrawMoneyCommand;
import com.dev.babacar.banking.domain.event.AccountOpenedEvent;
import com.dev.babacar.banking.domain.event.MoneyDepositedEvent;
import com.dev.babacar.banking.domain.event.MoneyWithdrawnEvent;
import org.axonframework.modelling.command.AggregateNotFoundException;
import org.axonframework.test.aggregate.AggregateTestFixture;
import org.axonframework.test.aggregate.FixtureConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class BankAccountTest {

    private static final String ACCOUNT_ID = "acc-123";
    private static final String OWNER = "Fatou Diop";

    private FixtureConfiguration<BankAccount> fixture;

    @BeforeEach
    void setUp() {
        fixture = new AggregateTestFixture<>(BankAccount.class);
    }

    private AccountOpenedEvent accountOpenedWith(double initialBalance) {
        return new AccountOpenedEvent(ACCOUNT_ID, OWNER, Money.of(initialBalance));
    }

    // ---------------- Ouverture ----------------

    @Test
    void devrait_ouvrir_un_compte_avec_un_solde_initial_valide() {
        fixture.givenNoPriorActivity()
                .when(new OpenAccountCommand(ACCOUNT_ID, OWNER, Money.of(500)))
                .expectSuccessfulHandlerExecution()
                .expectEvents(accountOpenedWith(500));
    }

    @Test
    void devrait_rejeter_un_solde_initial_negatif() {
        fixture.givenNoPriorActivity()
                .when(new OpenAccountCommand(ACCOUNT_ID, OWNER, Money.of(-100)))
                .expectException(IllegalArgumentException.class)
                .expectExceptionMessage("Le solde initial ne peut pas être négatif");
    }

    @Test
    void devrait_rejeter_un_nom_de_titulaire_vide() {
        fixture.givenNoPriorActivity()
                .when(new OpenAccountCommand(ACCOUNT_ID, "  ", Money.of(500)))
                .expectException(IllegalArgumentException.class);
    }

    // ---------------- Dépôt ----------------

    @Test
    void devrait_deposer_de_l_argent_sur_un_compte_existant() {
        fixture.given(accountOpenedWith(500))
                .when(new DepositMoneyCommand(ACCOUNT_ID, Money.of(200)))
                .expectSuccessfulHandlerExecution()
                .expectEvents(new MoneyDepositedEvent(ACCOUNT_ID, Money.of(200)));
    }

    @ParameterizedTest
    @ValueSource(doubles = {0, -50})
    void devrait_rejeter_un_depot_de_montant_nul_ou_negatif(double amount) {
        fixture.given(accountOpenedWith(500))
                .when(new DepositMoneyCommand(ACCOUNT_ID, Money.of(amount)))
                .expectException(IllegalArgumentException.class)
                .expectNoEvents();
    }

    @Test
    void devrait_rejeter_un_depot_sur_un_compte_inexistant() {
        fixture.givenNoPriorActivity()
                .when(new DepositMoneyCommand(ACCOUNT_ID, Money.of(100)))
                .expectException(AggregateNotFoundException.class);
    }

    // ---------------- Retrait ----------------

    @Test
    void devrait_retirer_de_l_argent_quand_le_solde_est_suffisant() {
        fixture.given(accountOpenedWith(500))
                .when(new WithdrawMoneyCommand(ACCOUNT_ID, Money.of(200)))
                .expectSuccessfulHandlerExecution()
                .expectEvents(new MoneyWithdrawnEvent(ACCOUNT_ID, Money.of(200)));
    }

    @Test
    void devrait_autoriser_un_retrait_egal_au_solde() {
        fixture.given(accountOpenedWith(500))
                .when(new WithdrawMoneyCommand(ACCOUNT_ID, Money.of(500)))
                .expectEvents(new MoneyWithdrawnEvent(ACCOUNT_ID, Money.of(500)));
    }

    @Test
    void devrait_rejeter_un_retrait_superieur_au_solde() {
        fixture.given(accountOpenedWith(100))
                .when(new WithdrawMoneyCommand(ACCOUNT_ID, Money.of(150)))
                .expectException(InsufficientBalanceException.class)
                .expectNoEvents();
    }

    @ParameterizedTest
    @ValueSource(doubles = {0, -50})
    void devrait_rejeter_un_retrait_de_montant_nul_ou_negatif(double amount) {
        fixture.given(accountOpenedWith(500))
                .when(new WithdrawMoneyCommand(ACCOUNT_ID, Money.of(amount)))
                .expectException(IllegalArgumentException.class)
                .expectNoEvents();
    }

    // ---------------- Replay sur plusieurs événements ----------------

    @Test
    void devrait_tenir_compte_de_tout_l_historique_pour_valider_un_retrait() {
        // Solde reconstruit : 100 + 200 = 300, donc 250 est autorisé
        fixture.given(
                        accountOpenedWith(100),
                        new MoneyDepositedEvent(ACCOUNT_ID, Money.of(200)))
                .when(new WithdrawMoneyCommand(ACCOUNT_ID, Money.of(250)))
                .expectEvents(new MoneyWithdrawnEvent(ACCOUNT_ID, Money.of(250)));
    }

    @Test
    void devrait_rejeter_un_retrait_quand_l_historique_a_deja_reduit_le_solde() {
        // Solde reconstruit : 100 + 200 - 250 = 50, donc 100 est refusé
        fixture.given(
                        accountOpenedWith(100),
                        new MoneyDepositedEvent(ACCOUNT_ID, Money.of(200)),
                        new MoneyWithdrawnEvent(ACCOUNT_ID, Money.of(250)))
                .when(new WithdrawMoneyCommand(ACCOUNT_ID, Money.of(100)))
                .expectException(InsufficientBalanceException.class)
                .expectNoEvents();
    }
}