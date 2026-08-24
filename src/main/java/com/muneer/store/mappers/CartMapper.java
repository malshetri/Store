package com.muneer.store.mappers;

import com.muneer.store.dtos.CartDto;
import com.muneer.store.entities.Cart;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CartMapper {
    CartDto toDto(Cart cart);
}
