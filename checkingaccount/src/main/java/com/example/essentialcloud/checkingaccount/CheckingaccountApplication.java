package com.example.essentialcloud.checkingaccount;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
public class CheckingaccountApplication {
	public static void main(String[] args) {
		SpringApplication.run(CheckingaccountApplication.class, args);
	}
}
