package com.itson.ms_comercio;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class MsComercioApplicationTests {

	@Test
	void contextLoads() {
	}

}
