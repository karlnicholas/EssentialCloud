package com.example.essentialcloud.checkingaccount;

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
public class CheckingAccountLedger {
    @Id @GeneratedValue
    private Long id;
    private Long checkingAccountId;
    private LocalDateTime timestamp;

    private String operation;
    private BigDecimal amount;
}
