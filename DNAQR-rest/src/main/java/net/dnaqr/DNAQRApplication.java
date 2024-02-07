package net.dnaqr;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class DNAQRApplication {

	public static void main(String[] args) {
		SpringApplication.run(DNAQRApplication.class, args);
	}
}
