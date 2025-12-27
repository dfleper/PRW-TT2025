package es.prw.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "es.prw")
@EnableJpaRepositories(basePackages = "es.prw.repository")
@EntityScan(basePackages = "es.prw.model")
public class Tt2025Application {

	public static void main(String[] args) {
		SpringApplication.run(Tt2025Application.class, args);
	}

}
