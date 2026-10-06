package tech.firas.lowcode;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class LowcodeApplication {

	public static void main(String[] args) {
		SpringApplication.run(LowcodeApplication.class, args);
	}

}
