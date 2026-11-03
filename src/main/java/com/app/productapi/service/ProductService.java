package com.app.productapi.service;

import com.app.productapi.client.DummyJsonClient;
import com.app.productapi.dto.ProductPageResponse;
import com.app.productapi.model.Product;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private final DummyJsonClient dummyJsonClient;

    public ProductService(DummyJsonClient dummyJsonClient) {
        this.dummyJsonClient = dummyJsonClient;
    }

    public List<Product> getAllProducts() {

        return dummyJsonClient.getAllProducts().getProducts();
    }

    public ProductPageResponse getProducts(int page, int size) {
        if (page < 0 || size <= 0) {
            throw new IllegalArgumentException(
                    "Page must be >= 0 and size must be > 0"
            );
        }
        List<Product> products = getAllProducts();
        int totalElements = products.size();
        int totalPages = (int) Math.ceil((double) totalElements / size);

        int startIndex = Math.min(page * size, totalElements);
        int endIndex = Math.min(startIndex + size, totalElements);
        List<Product> pageContent = products.subList(startIndex, endIndex);

        ProductPageResponse response = new ProductPageResponse();
        response.setContent(pageContent);
        response.setPage(page);
        response.setSize(size);
        response.setTotalElements(totalElements);
        response.setTotalPages(totalPages);
        response.setHasNext(page + 1 < totalPages);
        return response;
    }
}