package com.muneer.store.dtos;

import lombok.Data;

@Data
public class CheckOutResponse {
    private Long orderId;

    public CheckOutResponse(Long orderId) {
        this.orderId = orderId;

    }
}
