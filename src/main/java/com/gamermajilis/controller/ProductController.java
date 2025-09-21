package com.gamermajilis.controller;

import com.gamermajilis.service.ProductService;
import com.gamermajilis.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/products")
@Tag(name = "Marketplace", description = "Product marketplace endpoints")
@CrossOrigin(origins = "http://localhost:3000")
public class ProductController {

    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

    @Autowired
    private ProductService productService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping
    @Operation(summary = "Create product listing", description = "Create a new product listing in the marketplace")
    public ResponseEntity<Map<String, Object>> createProduct(
            HttpServletRequest request,
            @RequestParam @NotBlank @Size(min = 3, max = 200) String name,
            @RequestParam @NotBlank @Size(min = 10, max = 1000) String description,
            @RequestParam @NotNull @Positive BigDecimal price,
            @RequestParam(required = false, defaultValue = "USD") String currency,
            @RequestParam @NotNull String category,
            @RequestParam(required = false) String subcategory,
            @RequestParam @NotNull String condition,
            @RequestParam(required = false) String conditionDescription,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) String model,
            @RequestParam(required = false) String gameCompatibility,
            @RequestParam(required = false, defaultValue = "1") Integer quantityAvailable,
            @RequestParam(required = false, defaultValue = "STANDARD") String shippingMethod,
            @RequestParam(required = false, defaultValue = "0") BigDecimal shippingCost,
            @RequestParam(required = false, defaultValue = "false") Boolean freeShipping,
            @RequestParam(required = false) String shippingRegions,
            @RequestParam(required = false) Integer estimatedDeliveryDays,
            @RequestParam(required = false) String specifications,
            @RequestParam(required = false) String dimensions,
            @RequestParam(required = false) Double weight,
            @RequestParam(required = false) String color,
            @RequestParam(required = false) String tags,
            @RequestParam(required = false) String returnPolicy,
            @RequestParam(required = false) Integer warrantyPeriodDays,
            @RequestParam(required = false) String warrantyDescription) {
        
        try {
            Long userId = getUserIdFromRequest(request);
            if (userId == null) {
                return ResponseEntity.badRequest().body(createErrorResponse("Authentication required"));
            }

            Map<String, Object> productData = new HashMap<>();
            productData.put("name", name);
            productData.put("description", description);
            productData.put("price", price);
            productData.put("currency", currency);
            productData.put("category", category);
            productData.put("subcategory", subcategory);
            productData.put("condition", condition);
            productData.put("conditionDescription", conditionDescription);
            productData.put("brand", brand);
            productData.put("model", model);
            productData.put("gameCompatibility", gameCompatibility);
            productData.put("quantityAvailable", quantityAvailable);
            productData.put("shippingMethod", shippingMethod);
            productData.put("shippingCost", shippingCost);
            productData.put("freeShipping", freeShipping);
            productData.put("shippingRegions", shippingRegions);
            productData.put("estimatedDeliveryDays", estimatedDeliveryDays);
            productData.put("specifications", specifications);
            productData.put("dimensions", dimensions);
            productData.put("weight", weight);
            productData.put("color", color);
            productData.put("tags", tags);
            productData.put("returnPolicy", returnPolicy);
            productData.put("warrantyPeriodDays", warrantyPeriodDays);
            productData.put("warrantyDescription", warrantyDescription);

            Map<String, Object> response = productService.createProduct(userId, productData);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error creating product", e);
            return ResponseEntity.badRequest().body(createErrorResponse("Failed to create product"));
        }
    }

    @PostMapping("/{productId}/images")
    @Operation(summary = "Upload product images", description = "Upload images for a product listing")
    public ResponseEntity<Map<String, Object>> uploadProductImages(
            HttpServletRequest request,
            @PathVariable Long productId,
            @RequestParam("images") List<MultipartFile> images,
            @RequestParam(required = false, defaultValue = "false") boolean setMainImage) {
        
        try {
            Long userId = getUserIdFromRequest(request);
            if (userId == null) {
                return ResponseEntity.badRequest().body(createErrorResponse("Authentication required"));
            }

            if (images.isEmpty()) {
                return ResponseEntity.badRequest().body(createErrorResponse("No images provided"));
            }

            // Validate images
            for (MultipartFile image : images) {
                if (!isValidImageFile(image)) {
                    return ResponseEntity.badRequest().body(createErrorResponse("Invalid file type. Only JPG, PNG files are allowed"));
                }
                if (image.getSize() > 5 * 1024 * 1024) { // 5MB limit per image
                    return ResponseEntity.badRequest().body(createErrorResponse("Image file size must not exceed 5MB"));
                }
            }

            Map<String, Object> response = productService.uploadProductImages(userId, productId, images, setMainImage);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error uploading product images", e);
            return ResponseEntity.badRequest().body(createErrorResponse("Failed to upload images"));
        }
    }

    @GetMapping("/{productId}")
    @Operation(summary = "Get product details", description = "Get detailed information about a specific product")
    public ResponseEntity<Map<String, Object>> getProduct(@PathVariable Long productId) {
        try {
            Map<String, Object> response = productService.getProductDetails(productId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error getting product details for ID: " + productId, e);
            return ResponseEntity.badRequest().body(createErrorResponse("Product not found"));
        }
    }

    @GetMapping
    @Operation(summary = "Get products list", description = "Get paginated list of products in the marketplace")
    public ResponseEntity<Map<String, Object>> getProductsList(
            HttpServletRequest request,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String condition,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String sortOrder,
            @RequestParam(required = false, defaultValue = "false") boolean myProducts) {
        
        try {
            Long userId = null;
            if (myProducts) {
                userId = getUserIdFromRequest(request);
                if (userId == null) {
                    return ResponseEntity.badRequest().body(createErrorResponse("Authentication required"));
                }
            }

            Map<String, Object> filters = new HashMap<>();
            if (category != null) filters.put("category", category);
            if (condition != null) filters.put("condition", condition);
            if (minPrice != null) filters.put("minPrice", minPrice);
            if (maxPrice != null) filters.put("maxPrice", maxPrice);
            if (brand != null) filters.put("brand", brand);
            if (sortBy != null) filters.put("sortBy", sortBy);
            if (sortOrder != null) filters.put("sortOrder", sortOrder);
            if (userId != null) filters.put("sellerId", userId);

            Map<String, Object> response = productService.getProductsList(page, size, filters);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error getting products list", e);
            return ResponseEntity.badRequest().body(createErrorResponse("Failed to get products list"));
        }
    }

    @PutMapping("/{productId}")
    @Operation(summary = "Update product", description = "Update an existing product listing")
    public ResponseEntity<Map<String, Object>> updateProduct(
            HttpServletRequest request,
            @PathVariable Long productId,
            @RequestParam(required = false) @Size(min = 3, max = 200) String name,
            @RequestParam(required = false) @Size(min = 10, max = 1000) String description,
            @RequestParam(required = false) @Positive BigDecimal price,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String subcategory,
            @RequestParam(required = false) String condition,
            @RequestParam(required = false) String conditionDescription,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) String model,
            @RequestParam(required = false) String gameCompatibility,
            @RequestParam(required = false) Integer quantityAvailable,
            @RequestParam(required = false) String shippingMethod,
            @RequestParam(required = false) BigDecimal shippingCost,
            @RequestParam(required = false) Boolean freeShipping,
            @RequestParam(required = false) String tags,
            @RequestParam(required = false) String status) {
        
        try {
            Long userId = getUserIdFromRequest(request);
            if (userId == null) {
                return ResponseEntity.badRequest().body(createErrorResponse("Authentication required"));
            }

            Map<String, Object> updateData = new HashMap<>();
            if (name != null) updateData.put("name", name);
            if (description != null) updateData.put("description", description);
            if (price != null) updateData.put("price", price);
            if (category != null) updateData.put("category", category);
            if (subcategory != null) updateData.put("subcategory", subcategory);
            if (condition != null) updateData.put("condition", condition);
            if (conditionDescription != null) updateData.put("conditionDescription", conditionDescription);
            if (brand != null) updateData.put("brand", brand);
            if (model != null) updateData.put("model", model);
            if (gameCompatibility != null) updateData.put("gameCompatibility", gameCompatibility);
            if (quantityAvailable != null) updateData.put("quantityAvailable", quantityAvailable);
            if (shippingMethod != null) updateData.put("shippingMethod", shippingMethod);
            if (shippingCost != null) updateData.put("shippingCost", shippingCost);
            if (freeShipping != null) updateData.put("freeShipping", freeShipping);
            if (tags != null) updateData.put("tags", tags);
            if (status != null) updateData.put("status", status);

            Map<String, Object> response = productService.updateProduct(userId, productId, updateData);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error updating product", e);
            return ResponseEntity.badRequest().body(createErrorResponse("Failed to update product"));
        }
    }

    @DeleteMapping("/{productId}")
    @Operation(summary = "Delete product", description = "Delete a product listing")
    public ResponseEntity<Map<String, Object>> deleteProduct(
            HttpServletRequest request,
            @PathVariable Long productId) {
        
        try {
            Long userId = getUserIdFromRequest(request);
            if (userId == null) {
                return ResponseEntity.badRequest().body(createErrorResponse("Authentication required"));
            }

            Map<String, Object> response = productService.deleteProduct(userId, productId);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error deleting product", e);
            return ResponseEntity.badRequest().body(createErrorResponse("Failed to delete product"));
        }
    }

    @PostMapping("/{productId}/reviews")
    @Operation(summary = "Add product review", description = "Add a review for a product")
    public ResponseEntity<Map<String, Object>> addReview(
            HttpServletRequest request,
            @PathVariable Long productId,
            @RequestParam @NotNull @Positive Integer rating,
            @RequestParam @NotBlank @Size(min = 10, max = 1000) String comment,
            @RequestParam(required = false, defaultValue = "false") boolean verified) {
        
        try {
            Long userId = getUserIdFromRequest(request);
            if (userId == null) {
                return ResponseEntity.badRequest().body(createErrorResponse("Authentication required"));
            }

            if (rating < 1 || rating > 5) {
                return ResponseEntity.badRequest().body(createErrorResponse("Rating must be between 1 and 5"));
            }

            Map<String, Object> reviewData = new HashMap<>();
            reviewData.put("rating", rating);
            reviewData.put("comment", comment);
            reviewData.put("verified", verified);

            Map<String, Object> response = productService.addProductReview(userId, productId, reviewData);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error adding product review", e);
            return ResponseEntity.badRequest().body(createErrorResponse("Failed to add review"));
        }
    }

    @GetMapping("/{productId}/reviews")
    @Operation(summary = "Get product reviews", description = "Get reviews for a specific product")
    public ResponseEntity<Map<String, Object>> getProductReviews(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        try {
            Map<String, Object> response = productService.getProductReviews(productId, page, size);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error getting product reviews", e);
            return ResponseEntity.badRequest().body(createErrorResponse("Failed to get reviews"));
        }
    }

    @PostMapping("/{productId}/wishlist")
    @Operation(summary = "Toggle wishlist", description = "Add or remove product from wishlist")
    public ResponseEntity<Map<String, Object>> toggleWishlist(
            HttpServletRequest request,
            @PathVariable Long productId) {
        
        try {
            Long userId = getUserIdFromRequest(request);
            if (userId == null) {
                return ResponseEntity.badRequest().body(createErrorResponse("Authentication required"));
            }

            Map<String, Object> response = productService.toggleWishlist(userId, productId);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error toggling wishlist", e);
            return ResponseEntity.badRequest().body(createErrorResponse("Failed to toggle wishlist"));
        }
    }

    @PostMapping("/{productId}/view")
    @Operation(summary = "Record product view", description = "Record a view for the product")
    public ResponseEntity<Map<String, Object>> recordView(@PathVariable Long productId) {
        try {
            Map<String, Object> response = productService.recordProductView(productId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error recording product view", e);
            return ResponseEntity.badRequest().body(createErrorResponse("Failed to record view"));
        }
    }

    @GetMapping("/search")
    @Operation(summary = "Search products", description = "Search products by name, description, or tags")
    public ResponseEntity<Map<String, Object>> searchProducts(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice) {
        
        try {
            Map<String, Object> filters = new HashMap<>();
            if (category != null) filters.put("category", category);
            if (minPrice != null) filters.put("minPrice", minPrice);
            if (maxPrice != null) filters.put("maxPrice", maxPrice);

            Map<String, Object> response = productService.searchProducts(query, page, size, filters);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error searching products", e);
            return ResponseEntity.badRequest().body(createErrorResponse("Search failed"));
        }
    }

    @GetMapping("/categories")
    @Operation(summary = "Get product categories", description = "Get list of available product categories")
    public ResponseEntity<Map<String, Object>> getProductCategories() {
        try {
            Map<String, Object> response = productService.getProductCategories();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error getting product categories", e);
            return ResponseEntity.badRequest().body(createErrorResponse("Failed to get categories"));
        }
    }

    @GetMapping("/featured")
    @Operation(summary = "Get featured products", description = "Get featured products in the marketplace")
    public ResponseEntity<Map<String, Object>> getFeaturedProducts(
            @RequestParam(defaultValue = "10") int limit) {
        
        try {
            Map<String, Object> response = productService.getFeaturedProducts(limit);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error getting featured products", e);
            return ResponseEntity.badRequest().body(createErrorResponse("Failed to get featured products"));
        }
    }

    // Helper methods
    private Long getUserIdFromRequest(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }

        try {
            String token = authHeader.substring(7);
            if (!jwtUtil.validateToken(token)) {
                return null;
            }
            return jwtUtil.getUserIdFromToken(token);
        } catch (Exception e) {
            logger.warn("Invalid token in request", e);
            return null;
        }
    }

    private Map<String, Object> createErrorResponse(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", message);
        return response;
    }

    private boolean isValidImageFile(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType != null && (
            contentType.equals("image/jpeg") ||
            contentType.equals("image/png")
        );
    }
}
