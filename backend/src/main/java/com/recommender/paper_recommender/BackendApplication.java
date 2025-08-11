package com.recommender.paper_recommender;

import com.recommender.paper_recommender.repository.PaperRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class BackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(BackendApplication.class, args);
	}

	@Bean
	public CommandLineRunner testDatabaseConnection(PaperRepository paperRepository) {
		return args -> {
			System.out.println("==================================================");
			System.out.println("TESTING DATABASE CONNECTION...");
			long paperCount = paperRepository.count();
			System.out.println("SUCCESS: Found " + paperCount + " papers in the database.");
			System.out.println("==================================================");
		};
	}
}