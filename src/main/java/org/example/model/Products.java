package org.example.model;

public record Products(
        int productId,
        String productName,
        String category,
        double unitPrice,
        int stockQuantity,
        int reorderLevel
) {
}