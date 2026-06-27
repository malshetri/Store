package com.muneer.store.controllers;

import com.muneer.store.dtos.ProductDto;
import com.muneer.store.entities.Category;
import com.muneer.store.entities.Product;
import com.muneer.store.mappers.ProductMapper;
import com.muneer.store.repositories.ProductRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/products")
public class ProductController {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    @GetMapping
    public List<ProductDto> getAllProducts(
            @RequestParam(required = false, defaultValue = "", name = "catagoryId") Byte catagoryId){

        List<Product> products;
        if (catagoryId != null){
            products = productRepository.findByCategoryId(catagoryId);
        } else { products = productRepository.findAllWithCategory();
        }
        return products.stream().map(productMapper::toDto).toList();

    }
    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getProductById(@PathVariable Long id){
        Product product = productRepository.findById(id).orElse(null);
        if (product == null){
            return ResponseEntity.notFound().build();
        }
        else {
            return ResponseEntity.ok(productMapper.toDto(product));
        }
    }
}
