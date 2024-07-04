package SpringBootElasticSearch.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.AbstractElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.AbstractReactiveElasticsearchTemplate;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@Configuration
@EnableElasticsearchRepositories(basePackages = "SpringBootElasticSearch.config.repository")
public class Config  {

    @Value("${elasticsearch.url}")
    public String elasticsearchUrl;

//    @Override
//    public ClientConfiguration clientConfiguration() {
//        final ClientConfiguration config = ClientConfiguration.builder()
//                .connectedTo(elasticsearchUrl)
//                .build();
//
//        return RestClient.create
//    }

//    public ElasticsearchClient elasticsearchClient() {
//        return null;
//    }

    @Bean
    public RestClient restClient() {
        return RestClient.builder(HttpHost.create(elasticsearchUrl)).build();
    }

    @Bean
    public RestClientTransport restClientTransport(RestClient restClient) {
        return new RestClientTransport(restClient, new co.elastic.clients.json.jackson.JacksonJsonpMapper());
    }

    @Bean
    public ElasticsearchClient elasticsearchClient(RestClientTransport restClientTransport) {
        return new ElasticsearchClient(restClientTransport);
    }

    @Bean
    public ElasticsearchTemplate elasticsearchTemplate(ElasticsearchClient elasticsearchClient) {
        return new ElasticsearchTemplate(elasticsearchClient);
    }
}
