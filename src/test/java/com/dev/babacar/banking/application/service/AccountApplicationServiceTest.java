package com.dev.babacar.banking.application.service;

import com.dev.babacar.banking.domain.account.Money;
import com.dev.babacar.banking.domain.account.exception.InsufficientBalanceException;
import com.dev.babacar.banking.domain.command.DepositMoneyCommand;
import com.dev.babacar.banking.domain.command.OpenAccountCommand;
import com.dev.babacar.banking.domain.command.WithdrawMoneyCommand;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AccountApplicationServiceTest {

    @Mock
    private CommandGateway commandGateway;

    @InjectMocks
    private AccountApplicationService service;

    @Test
    void devrait_envoyer_une_commande_d_ouverture_et_retourner_l_identifiant() {
        String accountId = service.openAccount("Fatou Diop", Money.of(500));

        ArgumentCaptor<Object> captor = ArgumentCaptor.forClass(Object.class);
        verify(commandGateway).sendAndWait(captor.capture());

        assertThat(accountId).isNotBlank();
        assertThat(captor.getValue()).isInstanceOf(OpenAccountCommand.class);
        OpenAccountCommand command = (OpenAccountCommand) captor.getValue();
        assertThat(command.accountId()).isEqualTo(accountId);
        assertThat(command.ownerName()).isEqualTo("Fatou Diop");
        assertThat(command.initialBalance()).isEqualTo(Money.of(500));
    }

    @Test
    void devrait_envoyer_une_commande_de_depot() {
        service.deposit("acc-123", Money.of(200));

        verify(commandGateway).sendAndWait(new DepositMoneyCommand("acc-123", Money.of(200)));
    }

    @Test
    void devrait_envoyer_une_commande_de_retrait() {
        service.withdraw("acc-123", Money.of(100));

        verify(commandGateway).sendAndWait(new WithdrawMoneyCommand("acc-123", Money.of(100)));
    }

    @Test
    void devrait_laisser_remonter_l_exception_metier() {
        doThrow(new InsufficientBalanceException("acc-123", Money.of(50), Money.of(100)))
                .when(commandGateway).sendAndWait(any(Object.class));

        assertThatThrownBy(() -> service.withdraw("acc-123", Money.of(100)))
                .isInstanceOf(InsufficientBalanceException.class);
    }
}