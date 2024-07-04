package SpringBootElasticSearch.service;

import SpringBootElasticSearch.entity.Product;
//import SpringBootElasticSearch.repository.ProductRepo;
import SpringBootElasticSearch.repository.ProductRepository;
import co.elastic.clients.elasticsearch._types.aggregations.Aggregation;
import co.elastic.clients.elasticsearch._types.aggregations.AggregationBuilders;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.json.JsonData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.AggregationContainer;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

//import com.example.demo.elasticcrudapi.domain.Product;
//import com.example.demo.elasticcrudapi.repository.ProductRepository;
//import org.elasticsearch.common.unit.Fuzziness;
//import org.elasticsearch.index.query.QueryBuilder;
//import org.elasticsearch.index.query.QueryBuilders;
//import org.elasticsearch.search.aggregations.AggregationBuilders;
//import org.elasticsearch.search.aggregations.bucket.terms.Terms;
//import org.elasticsearch.search.aggregations.metrics.Avg;
import org.springframework.data.domain.PageRequest;
//import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.*;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.data.elasticsearch.client.elc.Queries.matchQuery;
import static org.springframework.data.elasticsearch.client.elc.Queries.termQuery;

@Service
public class ProductService {

    private static final String PRODUCT_INDEX = "products";

//    @Autowired
//    private ProductRepo productRepo;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ElasticsearchOperations elasticsearchOperations;

//    // READ
//    public Iterable<Product> getProducts() {
//        return productRepo.findAll();
//    }

//    // CREATE
//    public Product insertProduct(Product product) {
//        return productRepo.save(product);
//    }
//
//    // UPDATE
//    public Product updateProduct(Product product, int id) {
//        Product product1 = productRepo.findById(id).get();
//        product1.setPrice(product.getPrice());
//        return product1;
//    }
//
//    // DELETE
//    public void deleteProduct(int id) {
//        productRepo.deleteById(id);
//    }

        public String save(Product product ) {
            try {
                return productRepository.bulkSave( List.of(product));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }


        public Optional<Product> findById( String id ) throws IOException {
            return Optional.ofNullable(productRepository.findDocById(id));
        }
        public Iterable<Product> findAll() throws IOException {
            return productRepository.findAll();
        }
        public void deleteById( String id ) throws IOException {
            productRepository.deleteDocById(id);
        }
//        public void delete( Product product ) {
//            productRepository.delete(product);
//        }

        public List<Product> findProductsByBoolQuery( String category, BigDecimal minPrice, Boolean inStock ) {
            //Bool Query
            Query boolQuery = (Query) QueryBuilders.bool()
                    .must((List<co.elastic.clients.elasticsearch._types.query_dsl.Query>) QueryBuilders.term().field("category").value(category).build())
                    .should((List<co.elastic.clients.elasticsearch._types.query_dsl.Query>) QueryBuilders.range().field("price").lt((JsonData) minPrice).build())
                    .should((List<co.elastic.clients.elasticsearch._types.query_dsl.Query>) QueryBuilders.match().field("inStock").query(inStock).build()).build();

            SearchRequest searchRequest = SearchRequest.of(s -> s
                    .index("products")
                    .query((co.elastic.clients.elasticsearch._types.query_dsl.Query) boolQuery)
            );
            SearchHits<Product> searchHits = elasticsearchOperations
                    .search((Query) searchRequest, Product.class);
            return searchHits.stream().map(SearchHit::getContent).collect(Collectors.toList());
        }

        public List<Product> findByInStock( boolean inStock ) {
            Query termQuery = (Query) QueryBuilders.term().field("inStock").value(inStock).build();
            SearchRequest searchRequest = SearchRequest.of(s -> s
                    .index("products")
                    .query((co.elastic.clients.elasticsearch._types.query_dsl.Query) termQuery)
            );
            SearchHits<Product> searchHits = elasticsearchOperations.search((Query) searchRequest, Product.class);
            return searchHits.stream().map(SearchHit::getContent).collect(Collectors.toList());
        }

        public List<Product> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice) {
            Query rangeQuery = (Query) QueryBuilders.range().field("price").gte((JsonData) minPrice).lte((JsonData) maxPrice).build();
            SearchRequest searchRequest = SearchRequest.of(s -> s
                    .index("products")
                    .query((co.elastic.clients.elasticsearch._types.query_dsl.Query) rangeQuery)
            );
            SearchHits<Product> searchHits = elasticsearchOperations.search((Query) searchRequest, Product.class);
            return searchHits.stream().map(SearchHit::getContent).collect(Collectors.toList());
        }

        public List<Product> findProductsByName( final String searchKeyword ) {
//            //Search for an exact match of a term in a field:
//            Query termQuery = QueryBuilders.term("category", searchKeyword);
//            //Search for documents where a numeric field matches a specific value:
//            QueryBuilder termQueryBuilder1 = QueryBuilders.termQuery("price", 42);
//            //Search for documents where a boolean field matches a specific value:
//            QueryBuilder termQueryBuilder2 = QueryBuilders.termQuery("inStock", true);
//            // Perform a full-text search on a field with relevance scoring:
//            //Match documents where a text field contains any of the specified terms:
//            QueryBuilder matchQueryBuilder = QueryBuilders.matchQuery("name", searchKeyword);
//            //Match documents where a text field contains any of the specified terms with a minimum should match requirement:
//            QueryBuilder matchQueryBuilder1 = QueryBuilders.matchQuery("name", searchKeyword)
//                    .minimumShouldMatch("2");
//            //NativeSearch Query
//            NativeSearchQuery nativeSearchQuery = new NativeSearchQueryBuilder()
//                    .withQuery(matchQueryBuilder).build();

            Query matchQuery = (Query) QueryBuilders.match().field("name").query(searchKeyword).build();
            SearchRequest searchRequest = SearchRequest.of(s -> s
                    .index("products")
                    .query((co.elastic.clients.elasticsearch._types.query_dsl.Query) matchQuery)
            );

            //String Query
            Query stringSearchQuery = new StringQuery(
                    "{\"match\":{\"name\":{\"query\":\"" + searchKeyword + "\"}}}\"");
            //criteria query
            Criteria criteria = new Criteria("price")
                    .greaterThan(200.0)
                    .lessThan(400.0);
            Query criteriaSearchQuery = new CriteriaQuery(criteria);
            SearchHits<Product> searchHits = elasticsearchOperations
                    .search((Query) searchRequest, Product.class);
            return searchHits.stream().map(SearchHit::getContent).collect(Collectors.toList());
        }

        public List<String> fetchSuggestions( String nameKeyword ) {
            String lowercaseNameKeyword = nameKeyword.toLowerCase();
            Query wildcardQuery = (Query) QueryBuilders.wildcard().field("name").value(nameKeyword + "*").build();
            SearchRequest searchRequest = SearchRequest.of(s -> s
                    .index("products")
                    .query((co.elastic.clients.elasticsearch._types.query_dsl.Query) wildcardQuery)
                    .size(5)
            );
            SearchHits<Product> searchHits = elasticsearchOperations.search(
                    wildcardQuery, Product.class);
            return searchHits.getSearchHits().stream()
                    .map(searchHit -> searchHit.getContent().getName())
                    .collect(Collectors.toList());
        }
        public List<Product> fuzzySearch( String nameKeyword ) {
            //Create query on name field enabling fuzzy search
            Query fuzzinessQuery = (Query) QueryBuilders.match().field("name").query(nameKeyword).fuzziness("AUTO").build();

            SearchRequest searchRequest = SearchRequest.of(s -> s
                    .index("products")
                    .query((co.elastic.clients.elasticsearch._types.query_dsl.Query) fuzzinessQuery)
            );
      /* NativeSearchQuery multiMatchSearchQuery = new NativeSearchQueryBuilder()
               .withQuery(fuzzinessQueryBuilder).build();*/
            SearchHits<Product> searchHits = elasticsearchOperations
                    .search(fuzzinessQuery, Product.class);
            return searchHits.stream().map(SearchHit::getContent).collect(Collectors.toList());
        }


        public List<Product> multiMatchQuery( String nameKeyword ) {
            //Create query on multiple fields
            Query multiMatchQuery = (Query) QueryBuilders.multiMatch().fields("category", "name").query(nameKeyword).build();

            SearchRequest searchRequest = SearchRequest.of(s -> s
                    .index("products")
                    .query((co.elastic.clients.elasticsearch._types.query_dsl.Query) multiMatchQuery)
            );

            SearchHits<Product> searchHits = elasticsearchOperations
                    .search(multiMatchQuery, Product.class);
            return searchHits.stream().map(SearchHit::getContent).collect(Collectors.toList());
        }
}
