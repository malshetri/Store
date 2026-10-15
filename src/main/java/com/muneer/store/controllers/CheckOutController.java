package com.muneer.store.controllers;

import com.muneer.store.dtos.CheckOutRequest;
import com.muneer.store.dtos.CheckOutResponse;
import com.muneer.store.dtos.ErrorDto;
import com.muneer.store.entities.Order;
import com.muneer.store.entities.OrderItem;
import com.muneer.store.entities.OrderStatus;
import com.muneer.store.repositories.CartRepository;
import com.muneer.store.repositories.OrderRepository;
import com.muneer.store.services.AuthService;
import com.muneer.store.services.CartService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/checkout")
@AllArgsConstructor
public class CheckOutController {

    private final CartRepository cartRepository;
    private final AuthService authService;
    private final OrderRepository orderRepository;
    private final CartService cartService;

    @PostMapping
    public ResponseEntity<?> checkout(
            @Valid @RequestBody CheckOutRequest request){

        var cart = cartRepository.getCartWithItems(request.getCartId()).orElse(null);

        if (cart == null || cart.getItems() == null){
            return ResponseEntity.badRequest().body(new ErrorDto("Cart not found")
            );
        }
        if (cart.getItems().isEmpty()){
            return ResponseEntity.badRequest().body(new ErrorDto("Cart is empty")
            );
        }

        var order = new Order();
        order.setCustomer(authService.getCurrentUser());
        order.setStatus(OrderStatus.PENDING);
        order.setTotalPrice(cart.getTotalPrice());

          cart.getItems().forEach(item -> {
            var orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setTotalPrice(item.getTotalPrice());
            orderItem.setProduct(item.getProduct());
            orderItem.setQuantity(item.getQuantity());
            orderItem.setUnitPrice(item.getProduct().getPrice());
            order.getItems().add(orderItem);
        });

          orderRepository.save(order);

          cartService.clearCart(cart.getId());

          return ResponseEntity.ok(new CheckOutResponse(order.getId()));
    }
}
