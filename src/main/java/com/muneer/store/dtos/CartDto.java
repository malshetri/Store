package com.muneer.store.dtos;

import com.muneer.store.entities.CartItem;
import com.muneer.store.entities.Product;
import lombok.Data;

import java.math.BigDecimal;
import java.rmi.server.UID;
import java.util.*;

@Data
public class CartDto {
    private UUID id;
    private List<CartItemDto> items = new ArrayList<>();
    private BigDecimal totalPrice = BigDecimal.ZERO;



}
