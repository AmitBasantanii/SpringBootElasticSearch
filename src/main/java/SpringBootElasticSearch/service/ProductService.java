//package SpringBootElasticSearch.service;
//
//import SpringBootElasticSearch.entity.Product;
//import SpringBootElasticSearch.repository.ProductRepo;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//@Service
//public class ProductService {
//
//    @Autowired
//    private ProductRepo productRepo;
//
//    // READ
//    public Iterable<Product> getProducts() {
//        return productRepo.findAll();
//    }
//
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
//}
