package periskal.academy.backend;

import java.util.Arrays;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
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

			Module module1 = new Module("Intro kaartlezen", "Intro naar het navigeren met kaart",
					Arrays.asList(chapter1, chapter2));
			Module module2 = new Module("Advanced kaartlezen", "Advanced navigeren met kaart");

			Training training = new Training("ECDIS", "English", "Description", "Nederlands", "Beschrijving", "Deutsch",
					"Beschreibung", "Français", "La Description", Arrays.asList(module1, module2));

			module1.setTraining(training);
			module2.setTraining(training);

			chapter1.setModule(module1);
			chapter2.setModule(module1);

			trainingRepository.save(training);

			Chapter chapter3 = new Chapter("test3", "descriptiontest3", "texttest3");
			Module module3 = new Module("test intro", "Intro test", Arrays.asList(chapter3));
			Training training2 = new Training("test", "test", "test", "test", "test", "test", "test", "test", "test",
					Arrays.asList(module3));
			module3.setTraining(training2);
			chapter3.setModule(module3);
			trainingRepository.save(training2);
		}
	}
}
