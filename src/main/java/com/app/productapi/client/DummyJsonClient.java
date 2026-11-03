package com.app.productapi.client;

import com.app.productapi.dto.ProductListResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class DummyJsonClient {
    private final RestClient restClient;

        //constructor
        public DummyJsonClient(@Value("${dummyjson.base-url}") String baseUrl){
            this.restClient = RestClient.builder().baseUrl(baseUrl).build();
        }

        public ProductListResponse getAllProducts(){
            return restClient.get().uri("/products?limit=0").retrieve().body(ProductListResponse.class);
        }

}
