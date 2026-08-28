package com.muneer.store.services;

import com.muneer.store.dtos.CartDto;
import com.muneer.store.dtos.CartItemDto;
import com.muneer.store.entities.Cart;
import com.muneer.store.exeptions.CartNotFoundExeption;
import com.muneer.store.exeptions.ProductNotFoundExeption;
import com.muneer.store.mappers.CartMapper;
import com.muneer.store.repositories.CartRepository;
import com.muneer.store.repositories.ProductRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
public class CartService {

    private CartRepository cartRepository;
    private CartMapper cartMapper;
    private ProductRepository productRepository;

    public CartDto createCart(){
        var cart = new Cart();
        cartRepository.save(cart);

        return cartMapper.toDto(cart);
    }

    public CartItemDto addToCart(UUID cartId, Long productId){
        var cart = cartRepository.getCartWithItems(cartId).orElse(null);
        if (cart == null){
            throw  new CartNotFoundExeption();
        }
        var product = productRepository.findById(productId).orElse(null);
        if (product == null){
            throw new ProductNotFoundExeption();
        }
        var cartItem = cart.addItem(product);

        cartRepository.save(cart);
        return cartMapper.toDto(cartItem);
    }

    public CartDto getCart(UUID cartId){
        var cart = cartRepository.getCartWithItems(cartId).orElse(null);
        if (cart == null){
            throw new CartNotFoundExeption();
        }
        return cartMapper.toDto(cart);
    }

    public CartItemDto updateItem(UUID cartId,Long productId, Integer quantity){
        var cart = cartRepository.getCartWithItems(cartId).orElse(null);
        if (cart == null){
            throw new CartNotFoundExeption();
        }
        var cartItem = cart.getItem(productId);
        if (cartItem == null){
            throw new ProductNotFoundExeption();
        }
            cartItem.setQuantity(quantity);
        cartRepository.save(cart);
        return cartMapper.toDto(cartItem);
    }

    public void removeItem(UUID cartId, Long productId){
        var cart = cartRepository.getCartWithItems(cartId).orElse(null);
        if (cart == null){
            throw new CartNotFoundExeption();
        }
        cart.removeItem(productId);

        cartRepository.save(cart);

    }

    public void clearCart(UUID cartId){
        var cart = cartRepository.getCartWithItems(cartId).orElse(null);
        if (cart == null){
            throw new CartNotFoundExeption();
        }
        cart.clear();

        cartRepository.save(cart);

    }
}
