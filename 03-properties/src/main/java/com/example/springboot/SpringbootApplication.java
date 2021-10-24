package com.example.springboot;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.beans.factory.annotation.Value;

@SpringBootApplication
@Slf4j
public class SpringbootApplication {
	UliWarDa uliWarDa;

	public static void main(String[] args) {
		SpringApplication.run(SpringbootApplication.class, args);
		log.info("Uli war da");
	}

	@Configuration
	@PropertySource("classpath:application.properties")
	static class MyConfiguration {
		@Value("${uli}") String uli;
		@Bean UliWarDa uliWarDa() {
			return new UliWarDa(uli);
		}
	}
}
