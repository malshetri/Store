package com.muneer.store.exeptions;

public class CartNotFoundExeption extends RuntimeException {
    public CartNotFoundExeption(){
        super("Cart not found");
    }
}
