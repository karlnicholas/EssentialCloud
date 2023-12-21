package com.example.essentialcloud.bff;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransferModel {
    private String principalName;
    private String sourceRequestAccount;
    private String targetRequestAccount;
    private BigDecimal amount;
}
