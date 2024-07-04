package SpringBootElasticSearch.service;

import SpringBootElasticSearch.entity.Vehicle;
import SpringBootElasticSearch.helper.Indices;
import SpringBootElasticSearch.search.SearchRequestDTO;
import SpringBootElasticSearch.search.util.SearchUtil;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.ElasticsearchException;
import co.elastic.clients.elasticsearch.core.IndexRequest;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.core.GetRequest;
import co.elastic.clients.elasticsearch.core.GetResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

//@Service
public class VehicleService {
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final Logger LOG = LoggerFactory.getLogger(VehicleService.class);

    private final ElasticsearchClient client;

    @Autowired
    public VehicleService(ElasticsearchClient client) {
        this.client = client;
    }

    @PostConstruct
    public void tryToCreateIndices() {
        // Add index creation logic here if necessary
    }

    public List<Vehicle> search(final SearchRequestDTO dto) {
        final SearchRequest request = SearchUtil.buildSearchRequest(
                Indices.VEHICLE_INDEX,
                dto
        );

        return searchInternal(request);
    }

    public List<Vehicle> getAllVehiclesCreatedSince(final Date date) {
        final SearchRequest request = SearchUtil.buildSearchRequest(
                Indices.VEHICLE_INDEX,
                "created",
                date
        );

        return searchInternal(request);
    }

    public List<Vehicle> searchCreatedSince(final SearchRequestDTO dto, final Date date) {
        final SearchRequest request = SearchUtil.buildSearchRequest(
                Indices.VEHICLE_INDEX,
                String.valueOf(dto),
                date
        );

        return searchInternal(request);
    }

    private List<Vehicle> searchInternal(final SearchRequest request) {
        if (request == null) {
            LOG.error("Failed to build search request");
            return Collections.emptyList();
        }

        try {
            final SearchResponse<Vehicle> response = client.search(request, Vehicle.class);

            List<Vehicle> vehicles = new ArrayList<>();
            for (Hit<Vehicle> hit : response.hits().hits()) {
                vehicles.add(hit.source());
            }

            return vehicles;
        } catch (ElasticsearchException | IOException e) {
            LOG.error(e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    public Boolean index(final Vehicle vehicle) {
        try {
            final IndexRequest<Vehicle> request = IndexRequest.of(i -> i
                    .index(Indices.VEHICLE_INDEX)
                    .id(vehicle.getId())
                    .document(vehicle)
            );

            client.index(request);
            return true;
        } catch (ElasticsearchException | IOException e) {
            LOG.error(e.getMessage(), e);
            return false;
        }
    }

    public Vehicle getById(final String vehicleId) {
        try {
            final GetRequest request = GetRequest.of(g -> g
                    .index(Indices.VEHICLE_INDEX)
                    .id(vehicleId)
            );

            final GetResponse<Vehicle> response = client.get(request, Vehicle.class);
            if (!response.found()) {
                return null;
            }

            return response.source();
        } catch (ElasticsearchException | IOException e) {
            LOG.error(e.getMessage(), e);
            return null;
        }
    }
}
