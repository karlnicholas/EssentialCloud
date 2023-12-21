package com.example.essentialcloud.transferbroker;


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
public class BrokerAccount {
    @Id
    private Long id;

    private BigDecimal balance;
}
