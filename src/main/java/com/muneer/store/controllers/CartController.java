package com.muneer.store.controllers;

import com.muneer.store.dtos.AddItemToCartRequest;
import com.muneer.store.dtos.CartDto;
import com.muneer.store.dtos.CartItemDto;
import com.muneer.store.entities.Cart;
import com.muneer.store.entities.CartItem;
import com.muneer.store.mappers.CartMapper;
import com.muneer.store.repositories.CartRepository;
import com.muneer.store.repositories.ProductRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.UUID;

@AllArgsConstructor
@RestController
@RequestMapping("/carts")

public class CartController {


    private final CartRepository cartRepository;
    private final CartMapper cartMapper;
    private final ProductRepository productRepository;

    @PostMapping
    public ResponseEntity<CartDto> createCart(UriComponentsBuilder uriBuilder){
        var cart = new Cart();
        cartRepository.save(cart);

        var cartDto = cartMapper.toDto(cart);

        var uri =  uriBuilder.path("/carts/{id}").buildAndExpand(cartDto.getId()).toUri();
        return ResponseEntity.created(uri).body(cartDto);
    }
    @PostMapping("/{cartId}/items")
    public ResponseEntity<CartItemDto> addToCart(@PathVariable UUID cartId, @RequestBody AddItemToCartRequest request){

        var cart = cartRepository.getCartWithItems(cartId).orElse(null);
        if (cart == null){
            return ResponseEntity.notFound().build();
        }
        var product = productRepository.findById(request.getProductId()).orElse(null);
        if (product == null){
            return ResponseEntity.badRequest().build();
        }
        var cartItem = cart.getItems().stream().filter(cartItem1 -> cartItem1.getProduct().getId()
                .equals(request.getProductId())).findFirst().orElse(null);
        if (cartItem != null){
            cartItem.setQuantity(cartItem.getQuantity() + 1);
        }
        else {
            cartItem = new CartItem();
            cartItem.setCart(cart);
            cartItem.setProduct(product);
            cartItem.setQuantity(1);
            cart.getItems().add(cartItem);
        }
        cartRepository.save(cart);
        var cartItemDto = cartMapper.toDto(cartItem);
        return ResponseEntity.status(HttpStatus.CREATED).body(cartItemDto);
    }
    @GetMapping("/{cartId}")
    public ResponseEntity<CartDto> getCart(@PathVariable UUID cartId){
        var cart = cartRepository.getCartWithItems(cartId).orElse(null);
        if (cart == null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(cartMapper.toDto(cart));
    }


}
