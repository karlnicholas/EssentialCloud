package com.example.essentialcloud.transferbroker;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TranferRequestRepository extends JpaRepository<TransferRequest, Long> {
    List<TransferRequest> findAllByPrincipalName(@Param("principalName") String principalName);
}
