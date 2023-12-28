package com.example.essentialcloud.userinfo;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
@RequestMapping("/api/v1/")
public class UserinfoApplication {
	public static void main(String[] args) {
		SpringApplication.run(UserinfoApplication.class, args);
	}

	@Autowired
	UserInfoService userInfoService;
	@GetMapping(value = "/userinfo")
	public String getUserInfo(@RequestParam String authenticationId) throws JsonProcessingException {
		return userInfoService.retrieveUserInfoByAuthenticationId(authenticationId);
	}

}
