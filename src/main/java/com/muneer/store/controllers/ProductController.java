package com.muneer.store.controllers;

import com.muneer.store.dtos.ProductDto;
import com.muneer.store.entities.Category;
import com.muneer.store.entities.Product;
import com.muneer.store.mappers.ProductMapper;
import com.muneer.store.repositories.CategoryRepository;
import com.muneer.store.repositories.ProductRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/products")
public class ProductController {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final CategoryRepository categoryRepository;

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
    @PostMapping
    public ResponseEntity<ProductDto> createProduct(@RequestBody ProductDto productDto,
                                                    UriComponentsBuilder uriBuilder){
        var category = categoryRepository.findById(productDto.getCategoryId()).orElse(null);
        if (category == null){
            return ResponseEntity.notFound().build();
        }

        var product = productMapper.toEntity(productDto);
        product.setCategory(category);
        productRepository.save(product);
        productDto.setId(product.getId());

        var uri = uriBuilder.path("/products/{id}").buildAndExpand(productDto.getId()).toUri();

        return ResponseEntity.created(uri).body(productDto);
    }
    @PutMapping("/{id}")
    public ResponseEntity<ProductDto> updateProduct(
            @PathVariable Long id,
            @RequestBody ProductDto productDto
    ){
        var category = categoryRepository.findById(productDto.getCategoryId()).orElse(null);
        if (category == null){
            return ResponseEntity.badRequest().build();
        }

        var product = productRepository.findById(id).orElse(null);
        if (product == null){
            return ResponseEntity.notFound().build();
        }
        else {
            productDto.setId(product.getId());
            productMapper.update(productDto, product);
            product.setCategory(category);
            productRepository.save(product);

            return ResponseEntity.ok(productDto);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable long id){
        var product = productRepository.findById(id).orElse(null);
        if (product == null){
            return ResponseEntity.notFound().build();
        }
        else {
            productRepository.delete(product);

            return ResponseEntity.noContent().build();
        }
    }
}
