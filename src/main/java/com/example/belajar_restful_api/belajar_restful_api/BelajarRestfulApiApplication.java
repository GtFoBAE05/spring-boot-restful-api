package com.example.belajar_restful_api.belajar_restful_api;

import com.xendit.Xendit;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
@PropertySource("classpath:key.properties")
public class BelajarRestfulApiApplication {

	@Value("${xendit.api.key}")
	private String xenditApiKey;

	public static void main(String[] args) {

		SpringApplication.run(BelajarRestfulApiApplication.class, args);
	}

	@PostConstruct
	public void configureXendit() {
		Xendit.Opt.setApiKey(xenditApiKey);
	}

}
