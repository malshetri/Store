package com.muneer.store.exeptions;

public class CartEmptyExeption extends RuntimeException {
    public CartEmptyExeption(){
        super("Cart is empty");
    }
}
