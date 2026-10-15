package com.muneer.store.services;

import com.muneer.store.dtos.CheckoutRequest;
import com.muneer.store.dtos.CheckoutResponse;
import com.muneer.store.entities.Order;
import com.muneer.store.exeptions.CartEmptyExeption;
import com.muneer.store.exeptions.CartNotFoundExeption;
import com.muneer.store.repositories.CartRepository;
import com.muneer.store.repositories.OrderRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class CheckoutService {
    private final CartRepository cartRepository;
    private final AuthService authService;
    private final OrderRepository orderRepository;
    private final CartService cartService;

    public CheckoutResponse checkout(CheckoutRequest request){
        var cart = cartRepository.getCartWithItems(request.getCartId()).orElse(null);

        if (cart == null || cart.getItems() == null){
            throw  new CartNotFoundExeption();
        }
        if (cart.isEmpty()){
            throw new CartEmptyExeption();
        }

        var order = Order.fromCart(cart, authService.getCurrentUser());
        orderRepository.save(order);

        cartService.clearCart(cart.getId());

        return new CheckoutResponse(order.getId());
    }
}
