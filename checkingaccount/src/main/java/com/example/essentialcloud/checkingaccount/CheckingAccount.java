package com.example.essentialcloud.checkingaccount;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CheckingAccount {
    @Id
    private Long id;

    private BigDecimal balance;
}
