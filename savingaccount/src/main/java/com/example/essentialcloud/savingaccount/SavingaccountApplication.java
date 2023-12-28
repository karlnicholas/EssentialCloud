package com.example.essentialcloud.savingaccount;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
public class SavingaccountApplication {
	public static void main(String[] args) {
		SpringApplication.run(SavingaccountApplication.class, args);
	}
}
