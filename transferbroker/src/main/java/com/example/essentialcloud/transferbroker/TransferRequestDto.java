package com.example.essentialcloud.transferbroker;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransferRequestDto {
    private String sourceRequestAccount;
    private String targetRequestAccount;
    private String principalName;
    private String amount;
}
