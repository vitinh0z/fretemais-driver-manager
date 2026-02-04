package com.fretemais.drivermanager;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class DriverManagerApplicationTests {

	@Test
	void contextLoads() {
	}

}
