package periskal.academy.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan("periskal.academy.backend.jpa")

public class BackendApplication {
	
	public static void main(String[] args) {
		SpringApplication.run(BackendApplication.class, args);
	}

	
		
	
}
