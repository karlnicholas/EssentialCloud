package com.example.essentialcloud.checkingaccount;


import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class CheckingAccountService {
    private final CheckingAccountRepository checkingAccountRepository;
    private final CheckingAccountLedgerRepository checkingAccountLedgerRepository;

    public CheckingAccountService(CheckingAccountRepository checkingAccountRepository, CheckingAccountLedgerRepository checkingAccountLedgerRepository) {
        this.checkingAccountRepository = checkingAccountRepository;
        this.checkingAccountLedgerRepository = checkingAccountLedgerRepository;
    }

    @Transactional
    public Long withdraw(Long checkingAccountId, BigDecimal amount) {
        CheckingAccount checkingAccount = checkingAccountRepository.findById(checkingAccountId).orElseThrow();
        checkingAccount.setBalance(checkingAccount.getBalance().subtract(amount));
        checkingAccountRepository.save(checkingAccount);
        return checkingAccountLedgerRepository.save(CheckingAccountLedger.builder().checkingAccountId(checkingAccountId).timestamp(LocalDateTime.now()).operation("withdraw").amount(amount).build()).getId();
    }
    @Transactional
    public Long deposit(Long checkingAccountId, BigDecimal amount) {
        CheckingAccount checkingAccount = checkingAccountRepository.findById(checkingAccountId).orElseThrow();
        checkingAccount.setBalance(checkingAccount.getBalance().add(amount));
        checkingAccountRepository.save(checkingAccount);
        return checkingAccountLedgerRepository.save(CheckingAccountLedger.builder().checkingAccountId(checkingAccountId).timestamp(LocalDateTime.now()).operation("deposit").amount(amount).build()).getId();
    }

    public BigDecimal balance(Long checkingAccountId) {
        return checkingAccountRepository.findById(checkingAccountId)
                .map(CheckingAccount::getBalance)
                .orElseThrow();
    }

    public Long verifytransfer(Long transferId) {
        return checkingAccountLedgerRepository.findById(transferId)
                .map(CheckingAccountLedger::getId)
                .orElseThrow();
    }
}
