package com.eyeloveyou.biz;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
public class SpringBootProjectEly0527Application extends SpringBootServletInitializer {
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
		return builder.sources(SpringBootProjectEly0527Application.class);
	}
	public static void main(String[] args) {
		SpringApplication.run(SpringBootProjectEly0527Application.class, args);
	}

}
