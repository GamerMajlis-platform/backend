package com.gamermajilis;

import com.gamermajilis.controller.ProductController;
import com.gamermajilis.service.CustomUserDetailsService;
import com.gamermajilis.service.ProductService;
import com.gamermajilis.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
@Import(TestSecurityConfig.class)
public class ProductTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @BeforeEach
    void setUp() {
        Mockito.when(jwtUtil.validateToken(any())).thenReturn(true); // Mock token validation
        Mockito.when(jwtUtil.getUserIdFromToken(any())).thenReturn(1L); // Mock user ID extraction
    }

    @Test
    void testCreateProduct() throws Exception {
        Map<String, Object> mockResponse = new HashMap<>();
        mockResponse.put("success", true);
        mockResponse.put("message", "Product created successfully");

        Mockito.when(productService.createProduct(eq(1L), any())).thenReturn(mockResponse);

        mockMvc.perform(post("/products")
                .header("Authorization", "Bearer mockToken")
                .param("name", "Test Product")
                .param("description", "This is a test product")
                .param("price", "100.00")
                .param("currency", "USD")
                .param("category", "GAMING_CONSOLES")
                .param("condition", "NEW"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Product created successfully"));
    }

    @Test
    void testGetProductDetails() throws Exception {
        Map<String, Object> mockResponse = new HashMap<>();
        mockResponse.put("success", true);
        mockResponse.put("product", new HashMap<>());

        Mockito.when(productService.getProductDetails(eq(1L))).thenReturn(mockResponse);

        mockMvc.perform(get("/products/1")
                .header("Authorization", "Bearer mockToken"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.product").isEmpty());
    }

    @Test
    void testUploadProductImages() throws Exception {
        MockMultipartFile file = new MockMultipartFile("images", "test.jpg", "image/jpeg", "test data".getBytes());
        Map<String, Object> mockResponse = new HashMap<>();
        mockResponse.put("success", true);
        mockResponse.put("message", "Images uploaded successfully");

        Mockito.when(productService.uploadProductImages(eq(1L), eq(1L), any(), eq(false))).thenReturn(mockResponse);

        mockMvc.perform(multipart("/products/1/images")
                .file(file)
                .header("Authorization", "Bearer mockToken"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Images uploaded successfully"));
    }

    @Test
    void testUpdateProduct() throws Exception {
        Map<String, Object> mockResponse = new HashMap<>();
        mockResponse.put("success", true);
        mockResponse.put("message", "Product updated successfully");

        Mockito.when(productService.updateProduct(eq(1L), eq(1L), any())).thenReturn(mockResponse);

        mockMvc.perform(put("/products/1")
                .header("Authorization", "Bearer mockToken")
                .param("name", "Updated Product")
                .param("price", "150.00"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Product updated successfully"));
    }

    @Test
    void testDeleteProduct() throws Exception {
        Map<String, Object> mockResponse = new HashMap<>();
        mockResponse.put("success", true);
        mockResponse.put("message", "Product deleted successfully");

        Mockito.when(productService.deleteProduct(eq(1L), eq(1L))).thenReturn(mockResponse);

        mockMvc.perform(delete("/products/1")
                .header("Authorization", "Bearer mockToken"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Product deleted successfully"));
    }

    @Test
    void testSearchProducts() throws Exception {
        Map<String, Object> mockResponse = new HashMap<>();
        mockResponse.put("success", true);
        mockResponse.put("products", List.of());

        Mockito.when(productService.searchProducts(eq("test"), eq(0), eq(20), any())).thenReturn(mockResponse);

        mockMvc.perform(get("/products/search")
                .param("query", "test")
                .param("page", "0")
                .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.products").isEmpty());
    }
}