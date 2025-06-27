package com.aplikacja.Aplikacja.firmowa;
//Autor: Tomasz Moch Repozytorium źródłowe: https://github.com/Szarl3j/Aplikacja-firmowa
//Data rozpoczęcia: 04.03. 2021 rok
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
@RestController

public class AplikacjaFirmowaApplication {

	public static void main(String[] args) throws Exception {
		SpringApplication.run(AplikacjaFirmowaApplication.class, args);
	}

}
