package fr.jco.api;

import org.locationtech.spatial4j.io.jackson.ShapesAsGeoJSONModule;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiApplication.class, args);
	}

	@Bean
	public ShapesAsGeoJSONModule shapeAsJson() {
		return new ShapesAsGeoJSONModule();
	}

}
