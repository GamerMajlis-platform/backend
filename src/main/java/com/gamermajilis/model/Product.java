package com.gamermajilis.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "products")
public class Product {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank
    @Size(min = 3, max = 200)
    @Column(name = "name", nullable = false)
    private String name;
    
    @NotBlank
    @Size(min = 10, max = 1000)
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @NotNull
    @Positive
    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;
    
    @Column(name = "currency", length = 3, nullable = false)
    private String currency = "USD";
    
    @Column(name = "original_price", precision = 10, scale = 2)
    private BigDecimal originalPrice;
    
    @Column(name = "discount_percentage")
    private Integer discountPercentage;
    
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    private ProductCategory category;
    
    @Column(name = "subcategory")
    private String subcategory;
    
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "condition_type", nullable = false)
    private ProductCondition condition;
    
    @Column(name = "condition_description", columnDefinition = "TEXT")
    private String conditionDescription;
    
    @Column(name = "brand")
    private String brand;
    
    @Column(name = "model")
    private String model;
    
    @Column(name = "game_compatibility", columnDefinition = "TEXT")
    private String gameCompatibility; // JSON array of compatible games
    
    // Inventory and availability
    @Column(name = "quantity_available", nullable = false)
    private Integer quantityAvailable = 1;
    
    @Column(name = "quantity_sold", nullable = false)
    private Integer quantitySold = 0;
    
    @Column(name = "is_available", nullable = false)
    private Boolean isAvailable = true;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ProductStatus status = ProductStatus.DRAFT;
    
    // Seller information
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    private User seller;
    
    @Column(name = "seller_verified", nullable = false)
    private Boolean sellerVerified = false;
    
    // Media and images
    @Column(name = "main_image_url")
    private String mainImageUrl;
    
    @Column(name = "image_urls", columnDefinition = "TEXT")
    private String imageUrls; // JSON array of image URLs
    
    @Column(name = "video_url")
    private String videoUrl;
    
    // Shipping and logistics
    @Enumerated(EnumType.STRING)
    @Column(name = "shipping_method", nullable = false)
    private ShippingMethod shippingMethod = ShippingMethod.STANDARD;
    
    @Column(name = "shipping_cost", precision = 10, scale = 2)
    private BigDecimal shippingCost = BigDecimal.ZERO;
    
    @Column(name = "free_shipping", nullable = false)
    private Boolean freeShipping = false;
    
    @Column(name = "shipping_regions", columnDefinition = "TEXT")
    private String shippingRegions; // JSON array of supported regions
    
    @Column(name = "estimated_delivery_days")
    private Integer estimatedDeliveryDays;
    
    // Product specifications
    @Column(name = "specifications", columnDefinition = "TEXT")
    private String specifications; // JSON format
    
    @Column(name = "dimensions")
    private String dimensions; // e.g., "30x20x10 cm"
    
    @Column(name = "weight")
    private Double weight; // in grams
    
    @Column(name = "color")
    private String color;
    
    // Reviews and ratings
    @Column(name = "average_rating", precision = 3, scale = 2)
    private BigDecimal averageRating = BigDecimal.ZERO;
    
    @Column(name = "total_reviews", nullable = false)
    private Integer totalReviews = 0;
    
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ProductReview> reviews = new ArrayList<>();
    
    // Sales and promotion
    @Column(name = "is_featured", nullable = false)
    private Boolean isFeatured = false;
    
    @Column(name = "promotion_start")
    private LocalDateTime promotionStart;
    
    @Column(name = "promotion_end")
    private LocalDateTime promotionEnd;
    
    @Column(name = "promotion_description")
    private String promotionDescription;
    
    // Analytics and statistics
    @Column(name = "view_count", nullable = false)
    private Long viewCount = 0L;
    
    @Column(name = "wishlist_count", nullable = false)
    private Long wishlistCount = 0L;
    
    @Column(name = "inquiry_count", nullable = false)
    private Long inquiryCount = 0L;
    
    // Search and discovery
    @Column(name = "tags", columnDefinition = "TEXT")
    private String tags; // JSON array of tags
    
    @Column(name = "keywords", columnDefinition = "TEXT")
    private String keywords;
    
    // Moderation and safety
    @Column(name = "moderation_status", nullable = false)
    private String moderationStatus = "PENDING"; // PENDING, APPROVED, REJECTED
    
    @Column(name = "moderation_reason")
    private String moderationReason;
    
    @Column(name = "flagged_count", nullable = false)
    private Integer flaggedCount = 0;
    
    // Return and warranty
    @Column(name = "return_policy", columnDefinition = "TEXT")
    private String returnPolicy;
    
    @Column(name = "warranty_period_days")
    private Integer warrantyPeriodDays;
    
    @Column(name = "warranty_description", columnDefinition = "TEXT")
    private String warrantyDescription;
    
    // Timestamps
    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    @Column(name = "listed_at")
    private LocalDateTime listedAt;
    
    @Column(name = "sold_at")
    private LocalDateTime soldAt;
    
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
    
    // Constructors
    public Product() {}
    
    public Product(String name, String description, BigDecimal price, 
                  ProductCategory category, ProductCondition condition, User seller) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.category = category;
        this.condition = condition;
        this.seller = seller;
    }
    
    // Getters and Setters (I'll include the essential ones for brevity)
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public BigDecimal getPrice() {
        return price;
    }
    
    public void setPrice(BigDecimal price) {
        this.price = price;
    }
    
    public String getCurrency() {
        return currency;
    }
    
    public void setCurrency(String currency) {
        this.currency = currency;
    }
    
    public BigDecimal getOriginalPrice() {
        return originalPrice;
    }
    
    public void setOriginalPrice(BigDecimal originalPrice) {
        this.originalPrice = originalPrice;
    }
    
    public Integer getDiscountPercentage() {
        return discountPercentage;
    }
    
    public void setDiscountPercentage(Integer discountPercentage) {
        this.discountPercentage = discountPercentage;
    }
    
    public ProductCategory getCategory() {
        return category;
    }
    
    public void setCategory(ProductCategory category) {
        this.category = category;
    }
    
    public String getSubcategory() {
        return subcategory;
    }
    
    public void setSubcategory(String subcategory) {
        this.subcategory = subcategory;
    }
    
    public ProductCondition getCondition() {
        return condition;
    }
    
    public void setCondition(ProductCondition condition) {
        this.condition = condition;
    }
    
    public String getConditionDescription() {
        return conditionDescription;
    }
    
    public void setConditionDescription(String conditionDescription) {
        this.conditionDescription = conditionDescription;
    }
    
    public User getSeller() {
        return seller;
    }
    
    public void setSeller(User seller) {
        this.seller = seller;
    }
    
    public Boolean getSellerVerified() {
        return sellerVerified;
    }
    
    public void setSellerVerified(Boolean sellerVerified) {
        this.sellerVerified = sellerVerified;
    }
    
    public ProductStatus getStatus() {
        return status;
    }
    
    public void setStatus(ProductStatus status) {
        this.status = status;
    }
    
    public Integer getQuantityAvailable() {
        return quantityAvailable;
    }
    
    public void setQuantityAvailable(Integer quantityAvailable) {
        this.quantityAvailable = quantityAvailable;
    }
    
    public Boolean getIsAvailable() {
        return isAvailable;
    }
    
    public void setIsAvailable(Boolean isAvailable) {
        this.isAvailable = isAvailable;
    }
    
    public BigDecimal getAverageRating() {
        return averageRating;
    }
    
    public void setAverageRating(BigDecimal averageRating) {
        this.averageRating = averageRating;
    }
    
    public Integer getTotalReviews() {
        return totalReviews;
    }
    
    public void setTotalReviews(Integer totalReviews) {
        this.totalReviews = totalReviews;
    }
    
    public List<ProductReview> getReviews() {
        return reviews;
    }
    
    public void setReviews(List<ProductReview> reviews) {
        this.reviews = reviews;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public LocalDateTime getListedAt() {
        return listedAt;
    }
    
    public void setListedAt(LocalDateTime listedAt) {
        this.listedAt = listedAt;
    }
    
    // Helper methods
    public boolean isDeleted() {
        return this.deletedAt != null;
    }
    
    public boolean isActive() {
        return this.status == ProductStatus.ACTIVE && this.isAvailable;
    }
    
    public boolean isSold() {
        return this.status == ProductStatus.SOLD || this.quantityAvailable <= 0;
    }
    
    public boolean isOnSale() {
        return this.originalPrice != null && this.originalPrice.compareTo(this.price) > 0;
    }
    
    public boolean isPromotionActive() {
        LocalDateTime now = LocalDateTime.now();
        return this.promotionStart != null && this.promotionEnd != null
               && now.isAfter(this.promotionStart) && now.isBefore(this.promotionEnd);
    }
    
    public boolean isApproved() {
        return "APPROVED".equals(this.moderationStatus);
    }
    
    public void incrementViewCount() {
        this.viewCount++;
    }
    
    public void incrementWishlistCount() {
        this.wishlistCount++;
    }
    
    public void decrementWishlistCount() {
        if (this.wishlistCount > 0) {
            this.wishlistCount--;
        }
    }
    
    public void incrementInquiryCount() {
        this.inquiryCount++;
    }
    
    public void markAsSold() {
        this.status = ProductStatus.SOLD;
        this.soldAt = LocalDateTime.now();
        this.quantitySold++;
        this.quantityAvailable--;
    }
    
    public void addReview(ProductReview review) {
        this.reviews.add(review);
        this.totalReviews++;
        recalculateAverageRating();
    }
    
    private void recalculateAverageRating() {
        if (reviews.isEmpty()) {
            this.averageRating = BigDecimal.ZERO;
            return;
        }
        
        BigDecimal sum = reviews.stream()
                .map(review -> BigDecimal.valueOf(review.getRating()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        this.averageRating = sum.divide(BigDecimal.valueOf(reviews.size()), 2, java.math.RoundingMode.HALF_UP);
    }
    
    public boolean canEdit() {
        return this.status == ProductStatus.DRAFT || this.status == ProductStatus.ACTIVE;
    }
    
    public boolean hasWarranty() {
        return this.warrantyPeriodDays != null && this.warrantyPeriodDays > 0;
    }
    
    public BigDecimal getTotalPrice() {
        return this.freeShipping ? this.price : this.price.add(this.shippingCost);
    }
} 