package com.muneer.store.mappers;

import com.muneer.store.dtos.ProductDto;
import com.muneer.store.entities.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")

public interface ProductMapper {
    @Mapping(target = "categoryId", source = "category.id" )
    ProductDto toDto(Product product);

    Product toEntity(ProductDto productDto);

    void update(ProductDto productDto, @MappingTarget Product product);
}
