package com.bootcamp;

import com.bootcamp.service.KafkaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class UpdatesServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(UpdatesServiceApplication.class, args);
	}

}
