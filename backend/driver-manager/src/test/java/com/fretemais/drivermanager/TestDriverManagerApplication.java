package com.fretemais.drivermanager;

import org.springframework.boot.SpringApplication;

public class TestDriverManagerApplication {

	public static void main(String[] args) {
		// Usa H2 em memória ao invés de Testcontainers (não requer Docker)
		SpringApplication.from(DriverManagerApplication::main)
				.run(args);
	}

}
