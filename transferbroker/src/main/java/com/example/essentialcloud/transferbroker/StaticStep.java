package com.example.essentialcloud.transferbroker;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StaticStep {
    @Id
    private StaticStepEnum staticStep;
}
