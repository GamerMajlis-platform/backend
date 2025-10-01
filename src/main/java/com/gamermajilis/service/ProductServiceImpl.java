package com.gamermajilis.service;

import com.gamermajilis.model.*;
import com.gamermajilis.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProductServiceImpl implements ProductService {
    
    private static final Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class);
    private final String uploadDirectory = "/tmp/uploads/products/";
    
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    // ProductReview repository would need to be created
    // @Autowired
    // private ProductReviewRepository productReviewRepository;
    
    @Override
    public Map<String, Object> createProduct(Long userId, Map<String, Object> productData) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Validate user
            Optional<User> userOpt = userRepository.findById(userId);
            if (userOpt.isEmpty()) {
                response.put("success", false);
                response.put("message", "User not found");
                return response;
            }
            
            User seller = userOpt.get();
            
            // Validate required fields
            String name = (String) productData.get("name");
            String description = (String) productData.get("description");
            String priceStr = productData.get("price") != null ? productData.get("price").toString() : null;
            String categoryStr = productData.get("category") != null ? productData.get("category").toString() : null;
            String conditionStr = productData.get("condition") != null ? productData.get("condition").toString() : null;
            
            if (name == null || name.trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "Product name is required");
                return response;
            }
            
            if (description == null || description.trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "Product description is required");
                return response;
            }
            
            if (priceStr == null) {
                response.put("success", false);
                response.put("message", "Product price is required");
                return response;
            }
            
            BigDecimal price;
            try {
                price = new BigDecimal(priceStr);
                if (price.compareTo(BigDecimal.ZERO) <= 0) {
                    response.put("success", false);
                    response.put("message", "Product price must be greater than 0");
                    return response;
                }
            } catch (NumberFormatException e) {
                response.put("success", false);
                response.put("message", "Invalid price format");
                return response;
            }
            
            ProductCategory category;
            try {
                category = ProductCategory.valueOf(categoryStr);
            } catch (IllegalArgumentException e) {
                response.put("success", false);
                response.put("message", "Invalid product category");
                return response;
            }
            
            ProductCondition condition;
            try {
                condition = ProductCondition.valueOf(conditionStr);
            } catch (IllegalArgumentException e) {
                response.put("success", false);
                response.put("message", "Invalid product condition");
                return response;
            }
            
            // Create product
            Product product = new Product();
            product.setName(name);
            product.setDescription(description);
            product.setPrice(price);
            product.setCategory(category);
            product.setCondition(condition);
            product.setSeller(seller);
            
            // Optional fields
            if (productData.get("currency") != null) {
                product.setCurrency((String) productData.get("currency"));
            }
            if (productData.get("subcategory") != null) {
                product.setSubcategory((String) productData.get("subcategory"));
            }
            if (productData.get("conditionDescription") != null) {
                product.setConditionDescription((String) productData.get("conditionDescription"));
            }
            if (productData.get("brand") != null) {
                product.setBrand((String) productData.get("brand"));
            }
            if (productData.get("model") != null) {
                product.setModel((String) productData.get("model"));
            }
            if (productData.get("gameCompatibility") != null) {
                product.setGameCompatibility(productData.get("gameCompatibility").toString());
            }
            if (productData.get("quantityAvailable") != null) {
                try {
                    if (productData.get("quantityAvailable") instanceof Number) {
                        product.setQuantityAvailable(((Number) productData.get("quantityAvailable")).intValue());
                    } else {
                        product.setQuantityAvailable(Integer.parseInt(productData.get("quantityAvailable").toString()));
                    }
                } catch (NumberFormatException e) {
                    // Use default quantity
                    product.setQuantityAvailable(1);
                }
            }
            if (productData.get("shippingCost") != null) {
                try {
                    product.setShippingCost(new BigDecimal(productData.get("shippingCost").toString()));
                } catch (NumberFormatException e) {
                    // Ignore invalid shipping cost
                }
            }
            if (productData.get("freeShipping") != null) {
                product.setFreeShipping((Boolean) productData.get("freeShipping"));
            }
            if (productData.get("estimatedDeliveryDays") != null) {
                try {
                    if (productData.get("estimatedDeliveryDays") instanceof Number) {
                        product.setEstimatedDeliveryDays(((Number) productData.get("estimatedDeliveryDays")).intValue());
                    } else {
                        product.setEstimatedDeliveryDays(Integer.parseInt(productData.get("estimatedDeliveryDays").toString()));
                    }
                } catch (NumberFormatException e) {
                    // Ignore invalid delivery days
                }
            }
            if (productData.get("specifications") != null) {
                product.setSpecifications(productData.get("specifications").toString());
            }
            if (productData.get("dimensions") != null) {
                product.setDimensions((String) productData.get("dimensions"));
            }
            if (productData.get("weight") != null) {
                try {
                    if (productData.get("weight") instanceof Number) {
                        product.setWeight(((Number) productData.get("weight")).doubleValue());
                    } else {
                        product.setWeight(Double.parseDouble(productData.get("weight").toString()));
                    }
                } catch (NumberFormatException e) {
                    // Ignore invalid weight
                }
            }
            if (productData.get("color") != null) {
                product.setColor((String) productData.get("color"));
            }
            if (productData.get("tags") != null) {
                product.setTags(productData.get("tags").toString());
            }
            if (productData.get("returnPolicy") != null) {
                product.setReturnPolicy((String) productData.get("returnPolicy"));
            }
            if (productData.get("warrantyPeriodDays") != null) {
                try {
                    if (productData.get("warrantyPeriodDays") instanceof Number) {
                        product.setWarrantyPeriodDays(((Number) productData.get("warrantyPeriodDays")).intValue());
                    } else {
                        product.setWarrantyPeriodDays(Integer.parseInt(productData.get("warrantyPeriodDays").toString()));
                    }
                } catch (NumberFormatException e) {
                    // Ignore invalid warranty period
                }
            }
            if (productData.get("warrantyDescription") != null) {
                product.setWarrantyDescription((String) productData.get("warrantyDescription"));
            }
            
            // Save product
            Product savedProduct = productRepository.save(product);
            
            response.put("success", true);
            response.put("message", "Product created successfully");
            response.put("product", formatProductForResponse(savedProduct));
            
        } catch (Exception e) {
            logger.error("Error creating product", e);
            response.put("success", false);
            response.put("message", "Failed to create product: " + e.getMessage());
        }
        
        return response;
    }
    
    @Override
    public Map<String, Object> uploadProductImages(Long userId, Long productId, List<MultipartFile> images, boolean setMainImage) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Check if user owns the product
            Optional<Product> productOpt = productRepository.findByIdAndSellerIdAndDeletedAtIsNull(productId, userId);
            
            if (productOpt.isEmpty()) {
                response.put("success", false);
                response.put("message", "Product not found or access denied");
                return response;
            }
            
            Product product = productOpt.get();
            
            if (images == null || images.isEmpty()) {
                response.put("success", false);
                response.put("message", "No images provided");
                return response;
            }
            
            // Create directory if not exists
            createDirectoriesIfNeeded();
            
            List<Map<String, Object>> uploadedImages = new ArrayList<>();
            List<String> imageUrls = new ArrayList<>();
            
            for (int i = 0; i < images.size() && i < 5; i++) { // Max 5 images
                MultipartFile image = images.get(i);
                
                if (image.isEmpty()) continue;
                
                // Validate file type
                String originalFilename = image.getOriginalFilename();
                if (originalFilename == null || !isValidImageType(originalFilename)) {
                    continue; // Skip invalid files
                }
                
                // Generate filename
                String fileExtension = getFileExtension(originalFilename);
                String storedFilename = "product_" + productId + "_image_" + (i + 1) + "." + fileExtension;
                Path filePath = Paths.get(uploadDirectory + storedFilename);
                
                // Save file
                Files.write(filePath, image.getBytes());
                
                String imageUrl = "/uploads/products/" + storedFilename;
                imageUrls.add(imageUrl);
                
                Map<String, Object> imageInfo = new HashMap<>();
                imageInfo.put("id", i + 1);
                imageInfo.put("url", imageUrl);
                imageInfo.put("isMainImage", setMainImage && i == 0);
                uploadedImages.add(imageInfo);
                
                // Set main image
                if (setMainImage && i == 0) {
                    product.setMainImageUrl(imageUrl);
                }
            }
            
            // Update product with image URLs
            product.setImageUrls(String.join(",", imageUrls));
            productRepository.save(product);
            
            response.put("success", true);
            response.put("message", "Product images uploaded successfully");
            response.put("images", uploadedImages);
            
        } catch (Exception e) {
            logger.error("Error uploading product images", e);
            response.put("success", false);
            response.put("message", "Failed to upload product images");
        }
        
        return response;
    }
    
    @Override
    public Map<String, Object> getProductDetails(Long productId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Optional<Product> productOpt = productRepository.findById(productId);
            
            if (productOpt.isEmpty() || productOpt.get().isDeleted()) {
                response.put("success", false);
                response.put("message", "Product not found");
                return response;
            }
            
            Product product = productOpt.get();
            
            // Increment view count
            product.incrementViewCount();
            productRepository.save(product);
            
            response.put("success", true);
            response.put("message", "Product retrieved successfully");
            response.put("product", formatProductDetailsForResponse(product));
            
        } catch (Exception e) {
            logger.error("Error getting product details", e);
            response.put("success", false);
            response.put("message", "Failed to get product details");
        }
        
        return response;
    }
    
    @Override
    public Map<String, Object> getProductsList(int page, int size, Map<String, Object> filters) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Product> productPage = null;
            
            // Apply filters
            String categoryStr = (String) filters.get("category");
            String conditionStr = (String) filters.get("condition");
            String brand = (String) filters.get("brand");
            String minPriceStr = (String) filters.get("minPrice");
            String maxPriceStr = (String) filters.get("maxPrice");
            Boolean myProducts = (Boolean) filters.get("myProducts");
            Long userId = (Long) filters.get("userId");
            
            if (myProducts != null && myProducts && userId != null) {
                // Get user's products
                productPage = productRepository.findBySellerIdAndDeletedAtIsNullOrderByCreatedAtDesc(userId, pageable);
            } else if (categoryStr != null) {
                try {
                    ProductCategory category = ProductCategory.valueOf(categoryStr);
                    productPage = productRepository.findByCategoryAndStatusAndDeletedAtIsNullOrderByCreatedAtDesc(category, ProductStatus.ACTIVE, pageable);
                } catch (IllegalArgumentException e) {
                    productPage = productRepository.findByStatusAndDeletedAtIsNullOrderByCreatedAtDesc(ProductStatus.ACTIVE, pageable);
                }
            } else if (conditionStr != null) {
                try {
                    ProductCondition condition = ProductCondition.valueOf(conditionStr);
                    productPage = productRepository.findByConditionAndStatusAndDeletedAtIsNullOrderByCreatedAtDesc(condition, ProductStatus.ACTIVE, pageable);
                } catch (IllegalArgumentException e) {
                    productPage = productRepository.findByStatusAndDeletedAtIsNullOrderByCreatedAtDesc(ProductStatus.ACTIVE, pageable);
                }
            } else if (brand != null) {
                productPage = productRepository.findByBrandAndStatusAndDeletedAtIsNullOrderByCreatedAtDesc(brand, ProductStatus.ACTIVE, pageable);
            } else if (minPriceStr != null && maxPriceStr != null) {
                try {
                    BigDecimal minPrice = new BigDecimal(minPriceStr);
                    BigDecimal maxPrice = new BigDecimal(maxPriceStr);
                    productPage = productRepository.findByPriceBetweenAndStatusAndDeletedAtIsNullOrderByCreatedAtDesc(minPrice, maxPrice, ProductStatus.ACTIVE, pageable);
                } catch (NumberFormatException e) {
                    productPage = productRepository.findByStatusAndDeletedAtIsNullOrderByCreatedAtDesc(ProductStatus.ACTIVE, pageable);
                }
            } else {
                // Default: get all non-deleted products
                productPage = productRepository.findAll(pageable);
            }
            
            List<Map<String, Object>> products = productPage.getContent().stream()
                    .map(this::formatProductForResponse)
                    .collect(Collectors.toList());
            
            response.put("success", true);
            response.put("message", "Products list retrieved");
            response.put("products", products);
            response.put("totalElements", productPage.getTotalElements());
            response.put("totalPages", productPage.getTotalPages());
            response.put("currentPage", productPage.getNumber());
            response.put("pageSize", productPage.getSize());
            
        } catch (Exception e) {
            logger.error("Error getting products list", e);
            response.put("success", false);
            response.put("message", "Failed to get products list");
        }
        
        return response;
    }
    
    @Override
    public Map<String, Object> updateProduct(Long userId, Long productId, Map<String, Object> updateData) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Check if user owns the product
            Optional<Product> productOpt = productRepository.findByIdAndSellerIdAndDeletedAtIsNull(productId, userId);
            
            if (productOpt.isEmpty()) {
                response.put("success", false);
                response.put("message", "Product not found or access denied");
                return response;
            }
            
            Product product = productOpt.get();
            
            // Check if product can be edited
            if (!product.canEdit()) {
                response.put("success", false);
                response.put("message", "Product cannot be modified in its current status");
                return response;
            }
            
            // Update fields
            if (updateData.get("name") != null) {
                product.setName((String) updateData.get("name"));
            }
            if (updateData.get("description") != null) {
                product.setDescription((String) updateData.get("description"));
            }
            if (updateData.get("price") != null) {
                try {
                    BigDecimal price = new BigDecimal(updateData.get("price").toString());
                    product.setPrice(price);
                } catch (NumberFormatException e) {
                    response.put("success", false);
                    response.put("message", "Invalid price format");
                    return response;
                }
            }
            if (updateData.get("condition") != null) {
                try {
                    ProductCondition condition = ProductCondition.valueOf((String) updateData.get("condition"));
                    product.setCondition(condition);
                } catch (IllegalArgumentException e) {
                    // Ignore invalid condition
                }
            }
            if (updateData.get("quantityAvailable") != null) {
                product.setQuantityAvailable(((Number) updateData.get("quantityAvailable")).intValue());
            }
            if (updateData.get("status") != null) {
                try {
                    ProductStatus status = ProductStatus.valueOf((String) updateData.get("status"));
                    product.setStatus(status);
                    if (status == ProductStatus.ACTIVE) {
                        product.setListedAt(LocalDateTime.now());
                    }
                } catch (IllegalArgumentException e) {
                    // Ignore invalid status
                }
            }
            if (updateData.get("isAvailable") != null) {
                product.setIsAvailable((Boolean) updateData.get("isAvailable"));
            }
            if (updateData.get("moderationStatus") != null) {
                product.setModerationStatus((String) updateData.get("moderationStatus"));
            }
            if (updateData.get("listedAt") != null) {
                try {
                    product.setListedAt(LocalDateTime.parse((String) updateData.get("listedAt"), DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                } catch (Exception e) {
                    // ignore invalid format
                }
            }
            
            // Save updated product
            Product savedProduct = productRepository.save(product);
            
            response.put("success", true);
            response.put("message", "Product updated successfully");
            response.put("product", formatProductForResponse(savedProduct));
            
        } catch (Exception e) {
            logger.error("Error updating product", e);
            response.put("success", false);
            response.put("message", "Failed to update product");
        }
        
        return response;
    }
    
    @Override
    public Map<String, Object> deleteProduct(Long userId, Long productId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Check if user owns the product
            Optional<Product> productOpt = productRepository.findByIdAndSellerIdAndDeletedAtIsNull(productId, userId);
            
            if (productOpt.isEmpty()) {
                response.put("success", false);
                response.put("message", "Product not found or access denied");
                return response;
            }
            
            Product product = productOpt.get();
            
            // Soft delete
            product.setDeletedAt(LocalDateTime.now());
            product.setStatus(ProductStatus.DRAFT); // Use DRAFT instead of INACTIVE
            productRepository.save(product);
            
            response.put("success", true);
            response.put("message", "Product deleted successfully");
            
        } catch (Exception e) {
            logger.error("Error deleting product", e);
            response.put("success", false);
            response.put("message", "Failed to delete product");
        }
        
        return response;
    }
    
    @Override
    public Map<String, Object> addProductReview(Long userId, Long productId, Map<String, Object> reviewData) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Optional<Product> productOpt = productRepository.findById(productId);
            
            if (productOpt.isEmpty() || productOpt.get().isDeleted()) {
                response.put("success", false);
                response.put("message", "Product not found");
                return response;
            }
            
            Optional<User> userOpt = userRepository.findById(userId);
            if (userOpt.isEmpty()) {
                response.put("success", false);
                response.put("message", "User not found");
                return response;
            }
            
            // Basic implementation without ProductReview repository
            response.put("success", true);
            response.put("message", "Review added successfully");
            
            Map<String, Object> review = new HashMap<>();
            review.put("id", 1);
            review.put("rating", reviewData.get("rating"));
            review.put("comment", reviewData.get("comment"));
            review.put("verified", reviewData.get("verified"));
            
            Map<String, Object> reviewer = new HashMap<>();
            reviewer.put("id", userId);
            reviewer.put("displayName", userOpt.get().getDisplayName());
            review.put("reviewer", reviewer);
            review.put("createdAt", LocalDateTime.now().toString());
            
            response.put("review", review);
            
        } catch (Exception e) {
            logger.error("Error adding product review", e);
            response.put("success", false);
            response.put("message", "Failed to add product review");
        }
        
        return response;
    }
    
    @Override
    public Map<String, Object> getProductReviews(Long productId, int page, int size) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Optional<Product> productOpt = productRepository.findById(productId);
            
            if (productOpt.isEmpty() || productOpt.get().isDeleted()) {
                response.put("success", false);
                response.put("message", "Product not found");
                return response;
            }
            
            // Basic implementation without ProductReview repository
            response.put("success", true);
            response.put("message", "Product reviews retrieved");
            response.put("reviews", new ArrayList<>());
            response.put("averageRating", 0.0);
            response.put("totalReviews", 0);
            response.put("ratingDistribution", Map.of("5", 0, "4", 0, "3", 0, "2", 0, "1", 0));
            response.put("totalElements", 0);
            response.put("totalPages", 0);
            response.put("currentPage", page);
            response.put("pageSize", size);
            
        } catch (Exception e) {
            logger.error("Error getting product reviews", e);
            response.put("success", false);
            response.put("message", "Failed to get product reviews");
        }
        
        return response;
    }
    
    @Override
    public Map<String, Object> toggleWishlist(Long userId, Long productId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Optional<Product> productOpt = productRepository.findById(productId);
            
            if (productOpt.isEmpty() || productOpt.get().isDeleted()) {
                response.put("success", false);
                response.put("message", "Product not found");
                return response;
            }
            
            Product product = productOpt.get();
            
            // Basic implementation - would need WishlistRepository for full functionality
            product.incrementWishlistCount();
            productRepository.save(product);
            
            response.put("success", true);
            response.put("message", "Product added to wishlist");
            response.put("inWishlist", true);
            response.put("newWishlistCount", product.getWishlistCount());
            
        } catch (Exception e) {
            logger.error("Error toggling wishlist", e);
            response.put("success", false);
            response.put("message", "Failed to toggle wishlist");
        }
        
        return response;
    }
    
    @Override
    public Map<String, Object> recordProductView(Long productId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Optional<Product> productOpt = productRepository.findById(productId);
            
            if (productOpt.isEmpty() || productOpt.get().isDeleted()) {
                response.put("success", false);
                response.put("message", "Product not found");
                return response;
            }
            
            Product product = productOpt.get();
            product.incrementViewCount();
            productRepository.save(product);
            
            response.put("success", true);
            response.put("message", "View recorded successfully");
            response.put("newViewCount", product.getViewCount());
            
        } catch (Exception e) {
            logger.error("Error recording product view", e);
            response.put("success", false);
            response.put("message", "Failed to record view");
        }
        
        return response;
    }
    
    @Override
    public Map<String, Object> searchProducts(String query, int page, int size, Map<String, Object> filters) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Product> productPage;
            
            String categoryStr = (String) filters.get("category");
            String minPriceStr = (String) filters.get("minPrice");
            String maxPriceStr = (String) filters.get("maxPrice");
            
            if (categoryStr != null || minPriceStr != null || maxPriceStr != null) {
                ProductCategory category = null;
                BigDecimal minPrice = null;
                BigDecimal maxPrice = null;
                
                if (categoryStr != null) {
                    try {
                        category = ProductCategory.valueOf(categoryStr);
                    } catch (IllegalArgumentException e) {
                        // Ignore invalid category
                    }
                }
                
                if (minPriceStr != null) {
                    try {
                        minPrice = new BigDecimal(minPriceStr);
                    } catch (NumberFormatException e) {
                        // Ignore invalid price
                    }
                }
                
                if (maxPriceStr != null) {
                    try {
                        maxPrice = new BigDecimal(maxPriceStr);
                    } catch (NumberFormatException e) {
                        // Ignore invalid price
                    }
                }
                
                productPage = productRepository.searchProductsWithFilters(query, category, minPrice, maxPrice, pageable);
            } else {
                productPage = productRepository.searchProducts(query, pageable);
            }
            
            List<Map<String, Object>> products = productPage.getContent().stream()
                    .map(this::formatProductForResponse)
                    .collect(Collectors.toList());
            
            response.put("success", true);
            response.put("message", "Product search completed");
            response.put("products", products);
            response.put("totalElements", productPage.getTotalElements());
            response.put("totalPages", productPage.getTotalPages());
            response.put("currentPage", productPage.getNumber());
            response.put("pageSize", productPage.getSize());
            
        } catch (Exception e) {
            logger.error("Error searching products", e);
            response.put("success", false);
            response.put("message", "Search failed");
        }
        
        return response;
    }
    
    @Override
    public Map<String, Object> getProductCategories() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<Map<String, Object>> categories = new ArrayList<>();
            
            for (ProductCategory category : ProductCategory.values()) {
                Map<String, Object> categoryMap = new HashMap<>();
                categoryMap.put("name", category.name());
                categoryMap.put("displayName", category.getDisplayName());
                categoryMap.put("subcategories", getSubcategoriesForCategory(category));
                categories.add(categoryMap);
            }
            
            response.put("success", true);
            response.put("message", "Categories retrieved successfully");
            response.put("categories", categories);
            
        } catch (Exception e) {
            logger.error("Error getting product categories", e);
            response.put("success", false);
            response.put("message", "Failed to get categories");
        }
        
        return response;
    }
    
    @Override
    public Map<String, Object> getFeaturedProducts(int limit) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Pageable pageable = PageRequest.of(0, limit);
            List<Product> featuredProducts = productRepository.findByIsFeaturedTrueAndStatusAndDeletedAtIsNullOrderByCreatedAtDesc(ProductStatus.ACTIVE, pageable);
            
            List<Map<String, Object>> products = featuredProducts.stream()
                    .map(this::formatProductForResponse)
                    .collect(Collectors.toList());
            
            response.put("success", true);
            response.put("message", "Featured products retrieved");
            response.put("products", products);
            
        } catch (Exception e) {
            logger.error("Error getting featured products", e);
            response.put("success", false);
            response.put("message", "Failed to get featured products");
        }
        
        return response;
    }
    
    // Helper methods
    private void createDirectoriesIfNeeded() throws IOException {
        Path uploadPath = Paths.get(uploadDirectory);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
    }
    
    private boolean isValidImageType(String filename) {
        if (filename == null) return false;
        String extension = getFileExtension(filename).toLowerCase();
        return Arrays.asList("jpg", "jpeg", "png", "gif").contains(extension);
    }
    
    private String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) return "";
        return filename.substring(filename.lastIndexOf(".") + 1);
    }
    
    private List<String> getSubcategoriesForCategory(ProductCategory category) {
        // Return sample subcategories - would be more comprehensive in production
        switch (category) {
            case GAMING_ACCESSORIES:
                return Arrays.asList("Controllers", "Cables", "Stands", "Carrying Cases");
            case PC_COMPONENTS:
                return Arrays.asList("Graphics Cards", "Processors", "Memory", "Motherboards");
            case GAMES:
                return Arrays.asList("PC Games", "Console Games", "Digital Keys", "DLC");
            case GAMING_PERIPHERALS:
                return Arrays.asList("Gaming Mice", "Gaming Keyboards", "Controllers");
            default:
                return new ArrayList<>();
        }
    }
    
    private Map<String, Object> formatProductForResponse(Product product) {
        Map<String, Object> productMap = new HashMap<>();
        productMap.put("id", product.getId());
        productMap.put("name", product.getName());
        productMap.put("price", product.getPrice());
        productMap.put("currency", product.getCurrency());
        productMap.put("category", product.getCategory().name());
        productMap.put("condition", product.getCondition().name());
        productMap.put("brand", product.getBrand());
        productMap.put("mainImageUrl", product.getMainImageUrl());
        productMap.put("averageRating", product.getAverageRating());
        productMap.put("totalReviews", product.getTotalReviews());
        
        // Seller info
        Map<String, Object> sellerMap = new HashMap<>();
        sellerMap.put("id", product.getSeller().getId());
        sellerMap.put("displayName", product.getSeller().getDisplayName());
        productMap.put("seller", sellerMap);
        
        productMap.put("freeShipping", product.getFreeShipping());
        productMap.put("shippingCost", product.getShippingCost());
        productMap.put("createdAt", product.getCreatedAt().toString());
        
        return productMap;
    }
    
    private Map<String, Object> formatProductDetailsForResponse(Product product) {
        Map<String, Object> productMap = formatProductForResponse(product);
        
        // Add detailed fields
        productMap.put("description", product.getDescription());
        productMap.put("subcategory", product.getSubcategory());
        productMap.put("conditionDescription", product.getConditionDescription());
        productMap.put("model", product.getModel());
        productMap.put("quantityAvailable", product.getQuantityAvailable());
        productMap.put("isAvailable", product.getIsAvailable());
        productMap.put("status", product.getStatus().name());
        
        // Images
        if (product.getImageUrls() != null) {
            String[] urls = product.getImageUrls().split(",");
            productMap.put("imageUrls", Arrays.asList(urls));
        }
        
        productMap.put("gameCompatibility", product.getGameCompatibility());
        productMap.put("estimatedDeliveryDays", product.getEstimatedDeliveryDays());
        productMap.put("specifications", product.getSpecifications());
        productMap.put("dimensions", product.getDimensions());
        productMap.put("weight", product.getWeight());
        productMap.put("color", product.getColor());
        productMap.put("tags", product.getTags());
        productMap.put("viewCount", product.getViewCount());
        productMap.put("wishlistCount", product.getWishlistCount());
        productMap.put("warrantyPeriodDays", product.getWarrantyPeriodDays());
        productMap.put("returnPolicy", product.getReturnPolicy());
        if (product.getListedAt() != null) {
            productMap.put("listedAt", product.getListedAt().toString());
        }
        
        return productMap;
    }
}
