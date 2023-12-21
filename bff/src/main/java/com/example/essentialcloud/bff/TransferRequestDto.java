package com.example.essentialcloud.bff;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransferRequestDto {
    private String sourceRequest;
    private String sourceRequestAccount;
    private String targetRequest;
    private String targetRequestAccount;
    private String principalName;
    private String amount;
}
