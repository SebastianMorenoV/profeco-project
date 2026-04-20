package com.itson.ms_comercio;

import org.springframework.boot.SpringApplication;

public class TestMsComercioApplication {

	public static void main(String[] args) {
		SpringApplication.from(MsComercioApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
