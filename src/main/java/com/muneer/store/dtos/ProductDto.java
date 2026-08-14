package com.muneer.store.dtos;

import com.muneer.store.entities.Category;
import lombok.Data;


import java.math.BigDecimal;
@Data
public class ProductDto {
    private Long id;
    private String name;
    private BigDecimal price;
    private Byte categoryId;
    private String description;


}
