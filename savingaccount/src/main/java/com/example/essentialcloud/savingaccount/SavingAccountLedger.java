package com.example.essentialcloud.savingaccount;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SavingAccountLedger {
    @Id @GeneratedValue
    private Long id;
    private Long savingAccountId;
    private LocalDateTime timestamp;

    private String operation;
    private BigDecimal amount;
}
