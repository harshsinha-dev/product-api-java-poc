package com.app.productapi.service;

import com.app.productapi.client.DummyJsonClient;
import com.app.productapi.dto.ProductPageResponse;
import com.app.productapi.model.Product;
import org.springframework.stereotype.Service;

import java.util.Comparator;
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
    public ProductPageResponse getProducts(int page, int size, Map<String, String> filters, List<String> sort) {
        if (page < 1 || size <= 0) {
            throw new IllegalArgumentException(
                    "Page must be >= 1 and size must be > 0"
            );
        }
        List<Product> products = filterProducts(getAllProducts(), filters);
        products = sortProducts(products, sort);
        int totalElements = products.size();
        int totalPages = (int) Math.ceil((double) totalElements / size);

        int startIndex = Math.min((page-1) * size, totalElements);
        int endIndex = Math.min(startIndex + size, totalElements);
        List<Product> pageContent = products.subList(startIndex, endIndex);

        ProductPageResponse response = new ProductPageResponse();
        response.setContent(pageContent);
        response.setPage(page);
        response.setSize(size);
        response.setTotalElements(totalElements);
        response.setTotalPages(totalPages);
        response.setHasNext(page  < totalPages);
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

    //sorting products

    private List<Product> sortProducts(
            List<Product> products,
            List<String> sort) {

        if (sort == null || sort.isEmpty()) {
            return products;
        }

        Map<String, Comparator<Product>> comparators = Map.of(
                "id", Comparator.comparing(Product::getId),
                "title", Comparator.comparing(
                        Product::getTitle,
                        Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)),
                "price", Comparator.comparingDouble(Product::getPrice),
                "rating", Comparator.comparingDouble(Product::getRating),
                "stock", Comparator.comparingInt(Product::getStock),
                "category", Comparator.comparing(
                        Product::getCategory,
                        Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)),
                "brand", Comparator.comparing(
                        Product::getBrand,
                        Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER))
        );

        Comparator<Product> finalComparator = null;

        for (int i = 0; i < sort.size();) {

            String field;
            String direction;

            if (sort.get(i).contains(",")) {

                String[] parts = sort.get(i).split(",");

                if (parts.length != 2) {
                    throw new IllegalArgumentException(
                            "Invalid sort format. Use field,direction");
                }

                field = parts[0].trim();
                direction = parts[1].trim();
                i++;

            } else {

                if (i + 1 >= sort.size()) {
                    throw new IllegalArgumentException(
                            "Invalid sort format. Use field,direction");
                }

                field = sort.get(i).trim();
                direction = sort.get(i + 1).trim();
                i += 2;
            }

            Comparator<Product> comparator = comparators.get(field);

            if (comparator == null) {
                throw new IllegalArgumentException(
                        "Unsupported sort field: " + field);
            }

            if (direction.equalsIgnoreCase("desc")) {
                comparator = comparator.reversed();

            } else if (!direction.equalsIgnoreCase("asc")) {
                throw new IllegalArgumentException(
                        "Invalid sort direction: " + direction);
            }

            finalComparator = finalComparator == null
                    ? comparator
                    : finalComparator.thenComparing(comparator);
        }

        return products.stream()
                .sorted(finalComparator)
                .toList();
    }


}