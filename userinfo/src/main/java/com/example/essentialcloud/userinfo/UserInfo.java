package com.example.essentialcloud.userinfo;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

import java.io.Serializable;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserInfo implements Serializable {
    @Id
    private Long id;
    private String authenticationId;
    private Long checkingAccountId;
    private Long savingAccountId;
}
