package com.gamermajilis.service;

import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.Map;

public interface ProductService {
    
    Map<String, Object> createProduct(Long userId, Map<String, Object> productData);
    
    Map<String, Object> uploadProductImages(Long userId, Long productId, List<MultipartFile> images, boolean setMainImage);
    
    Map<String, Object> getProductDetails(Long productId);
    
    Map<String, Object> getProductsList(int page, int size, Map<String, Object> filters);
    
    Map<String, Object> updateProduct(Long userId, Long productId, Map<String, Object> updateData);
    
    Map<String, Object> deleteProduct(Long userId, Long productId);
    
    Map<String, Object> addProductReview(Long userId, Long productId, Map<String, Object> reviewData);
    
    Map<String, Object> getProductReviews(Long productId, int page, int size);
    
    Map<String, Object> toggleWishlist(Long userId, Long productId);
    
    Map<String, Object> recordProductView(Long productId);
    
    Map<String, Object> searchProducts(String query, int page, int size, Map<String, Object> filters);
    
    Map<String, Object> getProductCategories();
    
    Map<String, Object> getFeaturedProducts(int limit);
}
