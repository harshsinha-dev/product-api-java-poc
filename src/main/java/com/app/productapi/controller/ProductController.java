package com.app.productapi.controller;

import com.app.productapi.dto.ProductPageResponse;
import com.app.productapi.model.Product;
import com.app.productapi.service.ProductService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ProductPageResponse getProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String brand,
            HttpServletRequest request) {

        Set<String> allowedParams =
                Set.of("page", "size", "category", "brand");

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

        return productService.getProducts(page, size, filters);
    }
}