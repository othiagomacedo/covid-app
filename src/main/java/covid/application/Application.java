package covid.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import covid.application.api.util.Iniciador;

@SpringBootApplication
public class Application {

	public static void main(String[] args) throws Exception {
		Iniciador.start();
		SpringApplication.run(Application.class, args);
	}

}
