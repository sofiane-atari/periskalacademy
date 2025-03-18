package periskal.academy.backend;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;

import periskal.academy.backend.jpa.Chapter;
import periskal.academy.backend.jpa.Module;
import periskal.academy.backend.jpa.Training;
import periskal.academy.backend.jpa.TrainingRepository;

@SpringBootApplication
@EntityScan("periskal.academy.backend.jpa")

public class BackendApplication implements CommandLineRunner {
	private final TrainingRepository trainingRepository;

	public BackendApplication(TrainingRepository trainingRepository) {
		this.trainingRepository = trainingRepository;
	}

	public static void main(String[] args) {
		SpringApplication.run(BackendApplication.class, args);
	}

	@Override
	public void run(String... args) {
		if (trainingRepository.count() == 0) {
			Chapter chapter1 = new Chapter("test", "descriptiontest", "texttest");
			Chapter chapter2 = new Chapter("test2", "descriptiontest2", "texttest2");
			List<Chapter> chapters = Arrays.asList(chapter1, chapter2);
			Module product1 = new Module("Intro kaartlezen", "Intro naar het navigeren met kaart", chapters);
			Module product2 = new Module("Advanced kaartlezen", "Advanced navigeren met kaart");

			List<Module> products = Arrays.asList(product1, product2);
			Training training = new Training("Test", "test", products);
			trainingRepository.save(training);
		}
		// Test test = new Test(10, "hallo");
		// testrepository.save(test);
		// Test test2 = new Test(12, "test");
		// testrepository.save(test2);

		// Product product1 = new Product(1, "Intro kaartlezen", "Intro naar het
		// navigeren met kaart");
		// Product product2 = new Product(2, "Advanced kaartlezen", "Advanced navigeren
		// met kaart");

		// List<Product> products = Arrays.asList(product1, product2);
		// Training training1 = new Training(1, "Kaart navigatie", "Leren navigeren met
		// kaart", products);
		// trainingRepository.save(training1);
		// Product product3 = new Product(3, "Intro varen", "Leren opstarten met een
		// klein schip");
		// Product product4 = new Product(4, "Advanced varen", "Leren varen in complexe
		// situaties");
		// Product product5 = new Product(5, "Expert varen", "Varen door een omgeving
		// met weinig ruimte");
		// List<Product> vaarProducts = Arrays.asList(product3, product4, product5);
		// Training training2 = new Training(2, "Varen met schip", "Leren varen met
		// kleine schepen", vaarProducts);
		// trainingRepository.save(training2);

		// Product product6 = new Product(6, "Intro schipsoftware", "Leren kaartlezen in
		// software van Periskal");
		// Product product7 = new Product(7, "Advanced schipsoftware", "Leren gegevens
		// invoeren in software");
		// List<Product> softwareProducts = Arrays.asList(product6, product7);
		// Training training3 = new Training(3, "Schip software", "Software leren
		// begrijpen", softwareProducts);
		// trainingRepository.save(training3);
	}
}
