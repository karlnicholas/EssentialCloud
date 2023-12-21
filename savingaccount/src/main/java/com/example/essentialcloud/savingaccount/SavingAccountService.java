package com.example.essentialcloud.savingaccount;


import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class SavingAccountService {
    private final SavingAccountRepository savingAccountRepository;
    private final SavingAccountLedgerRepository savingAccountLedgerRepository;

    public SavingAccountService(SavingAccountRepository savingAccountRepository, SavingAccountLedgerRepository savingAccountLedgerRepository) {
        this.savingAccountRepository = savingAccountRepository;
        this.savingAccountLedgerRepository = savingAccountLedgerRepository;
    }

    @Transactional
    public Long withdraw(Long savingAccountId, BigDecimal amount) {
        SavingAccount savingAccount = savingAccountRepository.findById(savingAccountId).orElseThrow();
        savingAccount.setBalance(savingAccount.getBalance().subtract(amount));
        savingAccountRepository.save(savingAccount);
        return savingAccountLedgerRepository.save(SavingAccountLedger.builder().savingAccountId(savingAccountId).timestamp(LocalDateTime.now()).operation("withdraw").amount(amount).build()).getId();
    }
    @Transactional
    public Long deposit(Long savingAccountId, BigDecimal amount) {
        SavingAccount savingAccount = savingAccountRepository.findById(savingAccountId).orElseThrow();
        savingAccount.setBalance(savingAccount.getBalance().add(amount));
        savingAccountRepository.save(savingAccount);
        return savingAccountLedgerRepository.save(SavingAccountLedger.builder().savingAccountId(savingAccountId).timestamp(LocalDateTime.now()).operation("deposit").amount(amount).build()).getId();
    }

    public BigDecimal balance(Long savingAccountId) {
        return savingAccountRepository.findById(savingAccountId)
                .map(SavingAccount::getBalance)
                .orElseThrow();
    }

    public Long verifytransfer(Long transferId) {
        return savingAccountLedgerRepository.findById(transferId)
                .map(SavingAccountLedger::getId)
                .orElseThrow();
    }
}
