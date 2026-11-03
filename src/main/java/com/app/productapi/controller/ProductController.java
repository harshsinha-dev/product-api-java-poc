package com.app.productapi.controller;

import com.app.productapi.dto.ProductPageResponse;
import com.app.productapi.service.ProductService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ProductPageResponse getProducts(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) List<String> sort,
            HttpServletRequest request) {

        Set<String> allowedParams =
                Set.of("page", "size", "category", "brand", "sort");

        for (String param : request.getParameterMap().keySet()) {
            if (!allowedParams.contains(param)) {
                throw new IllegalArgumentException(
                        "Unsupported parameter: " + param
                );
            }
        }
        Map<String, String> filters = new HashMap<>();

        if (category != null) {
            filters.put("category", category);
        }

        if (brand != null) {
            filters.put("brand", brand);
        }
        System.out.println("Sort values: " + sort);
        return productService.getProducts(page, size, filters ,sort );
    }
}