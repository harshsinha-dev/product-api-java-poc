package com.app.productapi.controller;

import com.app.productapi.client.DummyJsonClient;
import com.app.productapi.dto.ProductListResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    private final DummyJsonClient dummyJsonClient;

    public TestController(DummyJsonClient dummyJsonClient){
        this.dummyJsonClient = dummyJsonClient;
    }

    @GetMapping("/api/test/products")
    public ProductListResponse testProducts(){
        return dummyJsonClient.getAllProducts();
    }
}
