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
		Main main = new Main();
		main.squareAdding();
		long start = System.currentTimeMillis();
		for (int i = 0; i < 100; i++) {
			main.cellAdding();
			main.tick();
		}
		for (int i = 0; i < 1000; i++) {
			main.tick();
		}
		System.out.println("Итоговый результат на 1100 ходов: " + (System.currentTimeMillis() - start));
//		Main.main(new String[]{});
	}
}
