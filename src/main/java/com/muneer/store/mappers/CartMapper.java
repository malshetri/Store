package com.muneer.store.mappers;

import com.muneer.store.dtos.CartDto;
import com.muneer.store.dtos.CartItemDto;
import com.muneer.store.entities.Cart;
import com.muneer.store.entities.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CartMapper {
    @Mapping(target = "totalPrice", expression = "java(cart.getTotalPrice() )")
    CartDto toDto(Cart cart);
    CartItemDto toDto(CartItem cartItem);
}
