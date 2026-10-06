package com.dev.babacar.banking.domain.account;

import com.dev.babacar.banking.domain.command.OpenAccountCommand;
import org.axonframework.test.aggregate.AggregateTestFixture;
import org.axonframework.test.aggregate.FixtureConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BankAccountTest {

    private FixtureConfiguration<BankAccount> fixture;

    @BeforeEach
    void setUp() {
        fixture = new AggregateTestFixture<>(BankAccount.class);
    }

    @Test
    void devrait_rejeter_un_solde_initial_negatif() {
        fixture.givenNoPriorActivity()
                .when(new OpenAccountCommand("acc-123", "Fatou Diop", Money.of(-100)))
                .expectException(IllegalArgumentException.class)
                .expectExceptionMessage("Le solde initial ne peut pas être négatif");
    }

    @Test
    void devrait_rejeter_un_nom_de_titulaire_vide() {
        fixture.givenNoPriorActivity()
                .when(new OpenAccountCommand("acc-123", "  ", Money.of(500)))
                .expectException(IllegalArgumentException.class);
    }
}