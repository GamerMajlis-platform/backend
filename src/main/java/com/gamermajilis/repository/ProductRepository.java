package com.gamermajilis.repository;

import com.gamermajilis.model.Product;
import com.gamermajilis.model.ProductCategory;
import com.gamermajilis.model.ProductCondition;
import com.gamermajilis.model.ProductStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    // Find products by seller
    Page<Product> findBySellerIdAndDeletedAtIsNullOrderByCreatedAtDesc(Long sellerId, Pageable pageable);
    
    // Find active products
    Page<Product> findByStatusAndDeletedAtIsNullOrderByCreatedAtDesc(ProductStatus status, Pageable pageable);
    
    // Find products by category
    Page<Product> findByCategoryAndStatusAndDeletedAtIsNullOrderByCreatedAtDesc(
        ProductCategory category, ProductStatus status, Pageable pageable);
    
    // Find products by condition
    Page<Product> findByConditionAndStatusAndDeletedAtIsNullOrderByCreatedAtDesc(
        ProductCondition condition, ProductStatus status, Pageable pageable);
    
    // Find products by brand
    Page<Product> findByBrandAndStatusAndDeletedAtIsNullOrderByCreatedAtDesc(
        String brand, ProductStatus status, Pageable pageable);
    
    // Find products by price range
    Page<Product> findByPriceBetweenAndStatusAndDeletedAtIsNullOrderByCreatedAtDesc(
        BigDecimal minPrice, BigDecimal maxPrice, ProductStatus status, Pageable pageable);
    
    // Find product by ID and seller for authorization
    Optional<Product> findByIdAndSellerIdAndDeletedAtIsNull(Long id, Long sellerId);
    
    // Find active product by ID
    Optional<Product> findByIdAndStatusAndDeletedAtIsNull(Long id, ProductStatus status);
    
    // Search products by name, description, or tags
    @Query("SELECT p FROM Product p WHERE p.deletedAt IS NULL AND p.status = 'ACTIVE' AND " +
           "(LOWER(p.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(p.description) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(p.tags) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(p.brand) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(p.model) LIKE LOWER(CONCAT('%', :query, '%'))) " +
           "ORDER BY p.createdAt DESC")
    Page<Product> searchProducts(@Param("query") String query, Pageable pageable);
    
    // Search products with filters
    @Query("SELECT p FROM Product p WHERE p.deletedAt IS NULL AND p.status = 'ACTIVE' " +
           "AND (:category IS NULL OR p.category = :category) " +
           "AND (:minPrice IS NULL OR p.price >= :minPrice) " +
           "AND (:maxPrice IS NULL OR p.price <= :maxPrice) " +
           "AND (LOWER(p.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(p.description) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(p.tags) LIKE LOWER(CONCAT('%', :query, '%'))) " +
           "ORDER BY p.createdAt DESC")
    Page<Product> searchProductsWithFilters(@Param("query") String query, 
                                          @Param("category") ProductCategory category,
                                          @Param("minPrice") BigDecimal minPrice,
                                          @Param("maxPrice") BigDecimal maxPrice,
                                          Pageable pageable);
    
    // Find featured products
    List<Product> findByIsFeaturedTrueAndStatusAndDeletedAtIsNullOrderByCreatedAtDesc(
        ProductStatus status, Pageable pageable);
    
    // Find products by game compatibility
    @Query("SELECT p FROM Product p WHERE p.deletedAt IS NULL AND p.status = 'ACTIVE' AND " +
           "LOWER(p.gameCompatibility) LIKE LOWER(CONCAT('%', :game, '%')) " +
           "ORDER BY p.createdAt DESC")
    Page<Product> findByGameCompatibility(@Param("game") String game, Pageable pageable);
    
    // Count products by seller
    long countBySellerIdAndDeletedAtIsNull(Long sellerId);
    
    // Find products by availability
    Page<Product> findByIsAvailableTrueAndStatusAndDeletedAtIsNullOrderByCreatedAtDesc(
        ProductStatus status, Pageable pageable);
    
    // Find recently listed products
    Page<Product> findByStatusAndDeletedAtIsNullOrderByListedAtDesc(ProductStatus status, Pageable pageable);
    
    // Find products with free shipping
    Page<Product> findByFreeShippingTrueAndStatusAndDeletedAtIsNullOrderByCreatedAtDesc(
        ProductStatus status, Pageable pageable);

    // Find all non-deleted products
    Page<Product> findByDeletedAtIsNullOrderByCreatedAtDesc(Pageable pageable);
}
