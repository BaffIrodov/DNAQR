package dnaqr.webapp;

import dnaqr.evoluringCore.Main;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class DNAQRApplication {

	public static void main(String[] args) {
		SpringApplication.run(DNAQRApplication.class, args);
		Main.main(new String[]{});
	}
}
