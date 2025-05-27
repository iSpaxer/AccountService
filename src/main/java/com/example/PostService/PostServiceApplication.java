package com.example.PostService;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class PostServiceApplication {

	@Bean
	CommandLineRunner runner(PersonRepository repository) {
		return args -> {

			Person person = new Person();
			person.setName("John");

			repository.save(person);
			Person saved = repository.findById(person.getId()).orElseThrow(RuntimeException::new);
		};
	}

	public static void main(String[] args) {
		SpringApplication.run(PostServiceApplication.class, args);
	}

}
