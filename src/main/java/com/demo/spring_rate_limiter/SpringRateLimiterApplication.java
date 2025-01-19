package com.demo.spring_rate_limiter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SpringRateLimiterApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringRateLimiterApplication.class, args);
	}

}
