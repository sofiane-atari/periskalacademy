package periskal.academy.backend;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import periskal.academy.backend.jpa.Test;
import periskal.academy.backend.jpa.TestRepository;

@SpringBootApplication
public class BackendApplication implements CommandLineRunner {
	private final TestRepository repository;

	public BackendApplication(TestRepository repository) {
		this.repository = repository;
	}

	public static void main(String[] args) {
		SpringApplication.run(BackendApplication.class, args);
	}

	@Override
	public void run(String... args) {
		Test test = new Test(10, "hallo");
		repository.save(test);
	}
}
