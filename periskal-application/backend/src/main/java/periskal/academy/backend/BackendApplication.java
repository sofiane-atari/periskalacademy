package periskal.academy.backend;

import java.util.Arrays;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import periskal.academy.backend.jpa.Product;
import periskal.academy.backend.jpa.Test;
import periskal.academy.backend.jpa.TestRepository;
import periskal.academy.backend.jpa.Training;
import periskal.academy.backend.jpa.TrainingRepository;

@SpringBootApplication
public class BackendApplication implements CommandLineRunner {
	private final TestRepository testrepository;
	private final TrainingRepository trainingRepository;

	public BackendApplication(TestRepository testrepository, TrainingRepository trainingRepository) {
		this.testrepository = testrepository;
		this.trainingRepository = trainingRepository;
	}

	public static void main(String[] args) {
		SpringApplication.run(BackendApplication.class, args);
	}

	@Override
	public void run(String... args) {
		Test test = new Test(10, "hallo");
		testrepository.save(test);
		Test test2 = new Test(12, "test");
		testrepository.save(test2);

		Product product1 = new Product(1, "Intro", "Intro naar het navigeren met kaart");
		Product product2 = new Product(2, "Advanced", "Advanced navigeren met kaart");

		List<Product> products = Arrays.asList(product1, product2);
		Training training1 = new Training(1, "Kaart navigatie", "Leren navigeren met kaart", products);
		trainingRepository.save(training1);

	}
}
