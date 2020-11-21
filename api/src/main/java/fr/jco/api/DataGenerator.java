package fr.jco.api;

import fr.jco.api.model.Town;
import fr.jco.api.repository.TownRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.io.WKTReader;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataGenerator implements ApplicationRunner {

	private final TownRepository repository;

	@Override
	public void run(ApplicationArguments args) throws Exception {
		Yaml yaml = new Yaml(new Constructor(TownYml.class));

		ClassPathResource resource = new ClassPathResource("data.yml");

		for (Object object : yaml.loadAll(resource.getInputStream())) {
			this.repository.save(((TownYml) object).asModel());
		}
	}

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class TownYml {
		private Long id;

		private String name;

		private Float latitude;

		private Float longitude;

		private String address;

		private String phone;

		private String webSite;

		@SneakyThrows
		public Town asModel() {
			return Town.builder()
					.id(this.id)
					.name(this.name)
					.point((Point) new WKTReader().read(String.format("POINT (%f %f)", this.latitude, this.longitude)))
					.address(this.address)
					.phone(this.phone)
					.webSite(this.webSite)
					.build();
		}
	}

}
