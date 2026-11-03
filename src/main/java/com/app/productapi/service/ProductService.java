package com.app.productapi.service;

import com.app.productapi.client.DummyJsonClient;
import com.app.productapi.dto.ProductPageResponse;
import com.app.productapi.model.Product;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ProductService {

    private final DummyJsonClient dummyJsonClient;

    public ProductService(DummyJsonClient dummyJsonClient) {
        this.dummyJsonClient = dummyJsonClient;
    }

    public List<Product> getAllProducts() {

        return dummyJsonClient.getAllProducts().getProducts();
    }

    //get products with pagination
    public ProductPageResponse getProducts(int page, int size, Map<String, String> filters) {
        if (page < 0 || size <= 0) {
            throw new IllegalArgumentException(
                    "Page must be >= 0 and size must be > 0"
            );
        }
        List<Product> products = filterProducts(getAllProducts(), filters);
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

    //filtering products
    private List<Product> filterProducts(
            List<Product> products,
            Map<String, String> filters) {


        String requestedCategory = filters.get("category");
        String requestedBrand = filters.get("brand");

        return products.stream()
                .filter(product -> {
                    boolean categoryMatches =
                            requestedCategory  == null ||
                                    (product.getCategory() != null &&
                                            requestedCategory .equalsIgnoreCase(product.getCategory()));
                    boolean brandMatches =
                            requestedBrand == null ||
                                    (product.getBrand() != null &&
                                            requestedBrand.equalsIgnoreCase(product.getBrand()));

                    return categoryMatches && brandMatches;
                })
                .toList();
    }



}