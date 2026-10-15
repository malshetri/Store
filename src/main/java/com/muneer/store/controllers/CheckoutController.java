package com.muneer.store.controllers;

import com.muneer.store.dtos.CheckoutRequest;

import com.muneer.store.dtos.CheckoutResponse;
import com.muneer.store.dtos.ErrorDto;
import com.muneer.store.exeptions.CartEmptyExeption;
import com.muneer.store.exeptions.CartNotFoundExeption;
import com.muneer.store.services.CheckoutService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/checkout")
@AllArgsConstructor
public class CheckoutController {

 private final CheckoutService checkoutService;

    @PostMapping
    public CheckoutResponse checkout(@Valid @RequestBody CheckoutRequest request){
        return checkoutService.checkout(request);
    }
    @ExceptionHandler({CartNotFoundExeption.class, CartEmptyExeption.class})
    public ResponseEntity<ErrorDto> handleExeption(Exception ex){
        return ResponseEntity.badRequest().body(new ErrorDto(ex.getMessage()));
    }
}
