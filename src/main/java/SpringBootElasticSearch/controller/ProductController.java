package SpringBootElasticSearch.controller;
//
//import SpringBootElasticSearch.entity.Product;
//import SpringBootElasticSearch.service.ProductService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequestMapping("/apis")
//public class ProductController {
//
//    @Autowired
//    private ProductService productService;
//
//    @GetMapping("/findAll")
//    Iterable<Product> findAll() {
//        return productService.getProducts();
//    }
//
//    @PostMapping("/insert")
//    public Product insertProduct(@RequestBody Product product){
//        return productService.insertProduct(product);
//    }
//}

import SpringBootElasticSearch.entity.Product;
import SpringBootElasticSearch.repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("api/products")
public class ProductController {

    @Autowired
    private ProductRepository elasticSearchQuery;

    @PostMapping
    public ResponseEntity<String> createOrUpdateDocument (@RequestBody Product product) throws IOException {
        String response = elasticSearchQuery.createOrUpdateDocument(product);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/bulk")
    public ResponseEntity<String> bulk (@RequestBody List<Product> product) throws IOException {
        String response = elasticSearchQuery.bulkSave(product);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{productId}")
    public ResponseEntity<Product> getDocumentId ( @PathVariable String productId) throws IOException {
        Product product = elasticSearchQuery.findDocById(productId);
        log.info("Product Document has been successfully retrieved.");
        return new ResponseEntity<>(product, HttpStatus.OK);
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<String> deleteDocumentById ( @PathVariable String productId) throws IOException {
        String message = elasticSearchQuery.deleteDocById(productId);
        log.info("Product document has been successfully deleted. Message : {}", message);
        return new ResponseEntity<>(message, HttpStatus.NO_CONTENT);
    }

    @GetMapping
    public ResponseEntity<List<Product>> findAll() throws IOException {
        List<Product> products = elasticSearchQuery.findAll();
        log.info("No of Product Document has been sucessfully retrieved : {}", products.size());
        return new ResponseEntity<>(products, HttpStatus.OK);
    }
}