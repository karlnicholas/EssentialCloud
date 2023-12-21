package com.example.essentialcloud.transferbroker;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransferRequest {
    @Id @GeneratedValue
    private Long id;
    private String sourceRequestAccount;
    private String targetRequestAccount;
    private String principalName;
    private String amount;
    private StaticStepEnum stepState;
    private Long transferId;
}
