package com.muneer.store.dtos;

import com.muneer.store.entities.Category;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@AllArgsConstructor
@Getter
public class ProductDto {
    private Long id;
    private String name;
    private BigDecimal price;
    private Byte categoryId;


}
