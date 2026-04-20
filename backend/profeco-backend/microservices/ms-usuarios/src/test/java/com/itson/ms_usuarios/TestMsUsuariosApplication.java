package com.itson.ms_usuarios;

import org.springframework.boot.SpringApplication;

public class TestMsUsuariosApplication {

	public static void main(String[] args) {
		SpringApplication.from(MsUsuariosApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
