package com.fateczl.BuffetRafaela;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@ComponentScan(basePackages = {"com.fateczl.BuffetRafaela"})
@EnableJpaRepositories(basePackages = {"com.fateczl.BuffetRafaela.repositories"})
@EntityScan(basePackages = {"com.fateczl.BuffetRafaela.entities"})
public class BuffetRafaelaApplication {

	public static void main(String[] args) {
		SpringApplication.run(BuffetRafaelaApplication.class, args);
	}

}
