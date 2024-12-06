package com.crud.ecom.proj.controller;

import com.crud.ecom.proj.model.Product;
import com.crud.ecom.proj.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class ProductController {

    @Autowired
    ProductService service;

    @RequestMapping("/")
    public String home(){
        return "Welcome to E-commnerce site";
    }

    @GetMapping("/products")
    public List<Product> getAllProducts(){
        return service.getAllProducts();
    }

    @GetMapping("/product/{prod_id}")
    public ResponseEntity<Product> getProductById(@PathVariable int prod_id){

        Product prod = service.getProductById(prod_id);
        if(prod == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        else
            return new ResponseEntity<>(prod,HttpStatus.OK);
    }

    @PostMapping("/product")
    public ResponseEntity<?> addProduct(@RequestPart Product product,
                                        @RequestPart MultipartFile imageFile) {
        try {
            Product prod = service.addNewProduct(product, imageFile);
            return new ResponseEntity<>(prod,HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/product/{prod_id}/image")
    public ResponseEntity<byte[]> getImageByProductId(@PathVariable int prod_id){
        Product prod = service.getProductById(prod_id); // to set the prod by the id
        byte[] imageFile = prod.getImageData(); //
        String imageType = prod.getImageType();

        return ResponseEntity.ok().body(imageFile);
    }

    @PutMapping("/product/{prod_id}")
    public ResponseEntity<String> updateProduct(@PathVariable int prod_id,
                                          @RequestPart Product product,
                                          @RequestPart MultipartFile imageFile){

        Product prod = null;
        try {
            prod = service.updateProduct(prod_id,product,imageFile);
        } catch (IOException e) {
            return new ResponseEntity<>("Failed to update",HttpStatus.BAD_REQUEST);
        }
        if(prod != null){
            return new ResponseEntity<>("Updated",HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Failed to update",HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/product/{prod_id}")
    public ResponseEntity<String> deleteProductById(@PathVariable int prod_id){
        Product product = service.getProductById(prod_id);
        if(product != null){
            service.deleteProductById(prod_id);
            return new ResponseEntity<>("Product with Id : "+ prod_id +" deleted",HttpStatus.OK);
        }else{
            return new ResponseEntity<>("Product not found!!",HttpStatus.NOT_FOUND);
        }
    }

    // search mapping
    @GetMapping("/products/search")
    public ResponseEntity<List<Product>> searchProducts(@RequestParam String keyword){
//        System.out.println(keyword);
        List<Product> products = service.searchProducts(keyword);
        return new ResponseEntity<>(products,HttpStatus.OK);
    }
}
