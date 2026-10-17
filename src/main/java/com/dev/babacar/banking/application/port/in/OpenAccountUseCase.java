package com.dev.babacar.banking.application.port.in;

import com.dev.babacar.banking.domain.account.Money;

public interface OpenAccountUseCase {

    /** Ouvre un compte et retourne son identifiant. */
    String openAccount(String ownerName, Money initialBalance);
}