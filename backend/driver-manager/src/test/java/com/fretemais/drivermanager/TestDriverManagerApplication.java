package com.fretemais.drivermanager;

import org.springframework.boot.SpringApplication;

public class TestDriverManagerApplication {

	public static void main(String[] args) {
		SpringApplication.from(DriverManagerApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
