package com.superkit.app.push;

import java.io.IOException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class AppPushApplication {

	public static void main(String[] args) throws IOException {
		SpringApplication.run(AppPushApplication.class, args);
	}

}
