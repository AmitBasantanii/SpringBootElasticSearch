package SpringBootElasticSearch.service;

import SpringBootElasticSearch.helper.Util;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.indices.CreateIndexRequest;
import co.elastic.clients.elasticsearch.indices.DeleteIndexRequest;
import co.elastic.clients.elasticsearch.indices.ExistsRequest;
import co.elastic.clients.transport.endpoints.BooleanResponse;
import co.elastic.clients.json.JsonData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import SpringBootElasticSearch.helper.Indices;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

//@Service
public class IndexService {
    private static final Logger LOG = LoggerFactory.getLogger(IndexService.class);
    private static final List<String> INDICES = List.of(Indices.VEHICLE_INDEX);
    private final ElasticsearchClient client;

    @Autowired
    public IndexService(ElasticsearchClient client) {
        this.client = client;
    }

    @PostConstruct
    public void tryToCreateIndices() {
        recreateIndices(false);
    }

    public void recreateIndices(final boolean deleteExisting) {
        final String settings = Util.loadAsString("static/es-settings.json");

        if (settings == null) {
            LOG.error("Failed to load index settings");
            return;
        }

        for (final String indexName : INDICES) {
            try {
                BooleanResponse indexExistsResponse = client.indices().exists(ExistsRequest.of(e -> e.index(indexName)));
                boolean indexExists = indexExistsResponse.value();

                if (indexExists) {
                    if (!deleteExisting) {
                        continue;
                    }

                    client.indices().delete(DeleteIndexRequest.of(d -> d.index(indexName)));
                }

                CreateIndexRequest createIndexRequest = CreateIndexRequest.of(c -> {
                    c.index(indexName)
                            .settings(s -> s.withJson((InputStream) JsonData.of(settings)));

                    final String mappings = loadMappings(indexName);
                    if (mappings != null) {
                        c.mappings(m -> m.withJson((InputStream) JsonData.of(mappings)));
                    }

                    return c;
                });

                client.indices().create(createIndexRequest);
            } catch (final IOException e) {
                LOG.error(e.getMessage(), e);
            }
        }
    }

    private String loadMappings(String indexName) {
        final String mappings = Util.loadAsString("static/mappings/" + indexName + ".json");
        if (mappings == null) {
            LOG.error("Failed to load mappings for index with name '{}'", indexName);
            return null;
        }

        return mappings;
    }
}
