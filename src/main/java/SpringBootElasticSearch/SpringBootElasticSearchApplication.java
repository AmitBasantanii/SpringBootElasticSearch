package SpringBootElasticSearch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@SpringBootApplication
public class SpringBootElasticSearchApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringBootElasticSearchApplication.class, args);
	}

}
