package com.muneer.store.controllers;

import com.muneer.store.dtos.AddItemToCartRequest;
import com.muneer.store.dtos.CartDto;
import com.muneer.store.dtos.CartItemDto;
import com.muneer.store.dtos.UpdateCartItemRequest;
import com.muneer.store.exeptions.CartNotFoundExeption;
import com.muneer.store.exeptions.ProductNotFoundExeption;
import com.muneer.store.services.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jdk.jfr.Description;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;
import java.util.UUID;

@AllArgsConstructor
@RestController
@RequestMapping("/carts")
@Tag(name = "Carts")

public class CartController {


    private final CartService cartService;

    @PostMapping
    @Operation(summary = "Create a new cart")
    public ResponseEntity<CartDto> createCart(UriComponentsBuilder uriBuilder){

        var cartDto = cartService.createCart();

        var uri =  uriBuilder.path("/carts/{id}").buildAndExpand(cartDto.getId()).toUri();
        return ResponseEntity.created(uri).body(cartDto);
    }
    @PostMapping("/{cartId}/items")
    @Operation(summary = "Add a product to the cart")
    public ResponseEntity<CartItemDto> addToCart(
            @Parameter(description = "The ID of the cart")
            @PathVariable UUID cartId,
            @RequestBody AddItemToCartRequest request){


        var cartItemDto = cartService.addToCart(cartId, request.getProductId());
        return ResponseEntity.status(HttpStatus.CREATED).body(cartItemDto);
    }
    @GetMapping("/{cartId}")
    @Operation(summary = "Get a cart")
    public CartDto getCart(
            @Parameter(description = "The ID of the cart")
            @PathVariable UUID cartId){
        return  cartService.getCart(cartId);
    }

    @PutMapping("/{cartId}/items/{productId}")
    @Operation(summary = "Update a product quantity in the cart")
    public CartItemDto updateItem(
            @Parameter(description = "The ID of the cart")
            @PathVariable("cartId") UUID cartId,
            @Parameter(description = "The ID of the product")
            @PathVariable("productId") Long productId,
            @Valid @RequestBody UpdateCartItemRequest request
    ){
        return cartService.updateItem(cartId, productId,  request.getQuantity());
    }

    @DeleteMapping("/{cartId}/items/{productId}")
    @Operation(summary = "Remove a product from the cart")
    public ResponseEntity<?> removeItem(
           @Parameter(description = "The ID of the cart")
           @PathVariable("cartId") UUID cartId,
           @Parameter(description = "The ID of the product")
           @PathVariable("productId") Long productId
    ){
        cartService.removeItem(cartId, productId);

        return ResponseEntity.noContent().build();
    }
    @DeleteMapping("/{cartId}/items")
    @Operation(summary = "Remove all products from the cart")
    public ResponseEntity<Void> clearCart(
            @Parameter(description = "The ID of the cart")
            @PathVariable UUID cartId){
        cartService.clearCart(cartId);
        return ResponseEntity.noContent().build();
    }
    @ExceptionHandler(CartNotFoundExeption.class)
    public ResponseEntity<Map<String,String>> handleCartNotFound(){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Cart Not Found"));
    }
    @ExceptionHandler(ProductNotFoundExeption.class)
    public ResponseEntity<Map<String,String>> handleProductNotFound(){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Product Not Found in the cart"));
    }
}




