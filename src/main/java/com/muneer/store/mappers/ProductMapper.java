package com.muneer.store.mappers;

import com.muneer.store.dtos.ProductDto;
import com.muneer.store.entities.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")

public interface ProductMapper {
    @Mapping(target = "categoryId", source = "category.id" )
    ProductDto toDto(Product product);
}
