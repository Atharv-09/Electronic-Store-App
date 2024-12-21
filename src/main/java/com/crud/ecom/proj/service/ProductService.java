package com.crud.ecom.proj.service;

import com.crud.ecom.proj.model.Product;
import com.crud.ecom.proj.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
@Service
public class ProductService {

    @Autowired
    ProductRepository repo;

    public List<Product> getAllProducts() {
        return repo.findAll();
    }

    public Product addNewProduct(Product product, MultipartFile imageFile) throws IOException {

        // we have to send the image also
        product.setImageName(imageFile.getOriginalFilename());
        product.setImageType(imageFile.getContentType());
        product.setImageData(imageFile.getBytes());
        System.out.println("Product added : " + product.getName());
        return repo.save(product);
    }

    public Product getProductById(int prodId) {
        return repo.findById(prodId).orElse(null);
    }

    public Product updateProduct(int prod_id, Product product, MultipartFile imageFile) throws IOException {
        // we can create the check if the product is already present or not
        product.setImageData(imageFile.getBytes());
        product.setImageType(imageFile.getContentType());
        product.setImageName(imageFile.getOriginalFilename());
        return repo.save(product);
    }

    public void deleteProductById(int prodId) {
        repo.deleteById(prodId);
    }

    public List<Product> searchProducts(String keyword) {
        return repo.searchProducts(keyword);
    }
}
