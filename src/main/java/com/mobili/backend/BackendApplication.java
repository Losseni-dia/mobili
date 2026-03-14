package com.mobili.backend;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BackendApplication {

	public static void main(String[] args) {
		// 1. Charger le fichier .env
		Dotenv dotenv = Dotenv.configure()
				.ignoreIfMissing()
				.load();

		// 2. Injecter chaque variable du .env dans les propriétés système de Java
		dotenv.entries().forEach(entry -> {
			System.setProperty(entry.getKey(), entry.getValue());
		});

		// 3. Lancer l'application normalement
		SpringApplication.run(BackendApplication.class, args);
	}
}