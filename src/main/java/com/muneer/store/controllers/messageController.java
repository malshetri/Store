package com.muneer.store.controllers;

import com.muneer.store.entities.Message;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class messageController {

    @RequestMapping("/hello")
    public Message sayHello(){
        return new Message("Hello World");
    }
}
