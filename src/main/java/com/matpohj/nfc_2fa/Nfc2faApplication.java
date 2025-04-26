package com.matpohj.nfc_2fa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Nfc2faApplication {

	public static void main(String[] args) {
		// Set dev profile by default if no profile is active
		System.setProperty("spring.profiles.default", "dev");
		SpringApplication.run(Nfc2faApplication.class, args);
	}

}
