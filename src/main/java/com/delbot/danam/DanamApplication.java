package com.delbot.danam;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

@SpringBootApplication
@PropertySources({
	@PropertySource("classpath:secret.properties")
})
public class DanamApplication {

	public static void main(String[] args) {
		SpringApplication.run(DanamApplication.class, args);
	}

}
