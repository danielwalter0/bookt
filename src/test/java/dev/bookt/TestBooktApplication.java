package dev.bookt;

import org.springframework.boot.SpringApplication;

public class TestBooktApplication {

	public static void main(String[] args) {
		SpringApplication.from(BooktApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
