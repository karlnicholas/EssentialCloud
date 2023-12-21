package com.example.essentialcloud.userinfo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserInfoRepository extends JpaRepository<UserInfo, Long> {
    Optional<UserInfo> findUserInfoByAuthenticationId(@Param("authenticationId") String authenticationId);
}
