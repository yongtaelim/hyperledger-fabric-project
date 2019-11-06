package fabric.stress.test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class StressTestApplication {
	public static void main(String[] args) throws Exception {
		setProfiles();
		SpringApplication.run(StressTestApplication.class, args);
	}
	
	private static void setProfiles() {
		String profile = System.getProperty("spring.profiles.active");
        if(profile == null) {
            System.setProperty("spring.profiles.active", "local");
        }
	}
}
