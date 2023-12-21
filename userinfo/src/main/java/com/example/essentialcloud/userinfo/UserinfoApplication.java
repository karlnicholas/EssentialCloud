package com.example.essentialcloud.userinfo;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
@RequestMapping("/api/v1/")
@EnableCaching
@Configuration
@EnableWebSecurity
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

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.authorizeHttpRequests(authorize -> authorize
				.anyRequest().authenticated()
			)
			.oauth2ResourceServer((oauth2) -> oauth2.jwt(Customizer.withDefaults()));
		return http.build();
	}
}
