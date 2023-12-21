package com.example.essentialcloud.checkingaccount;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CheckingAccountLedgerRepository extends JpaRepository<CheckingAccountLedger, Long> {
}
