package com.thecheatschool.thecheatschool.server;

import de.codecentric.boot.admin.server.config.EnableAdminServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAdminServer
@EnableCaching
@EnableAsync
public class ThecheatschoolserverApplication {
	public static void main(String[] args) {
		SpringApplication.run(ThecheatschoolserverApplication.class, args);
	}
}