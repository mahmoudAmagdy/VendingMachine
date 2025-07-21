package com.mam.vm;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;


import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.context.WebApplicationContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mam.vm.entities.Role;
import com.mam.vm.entities.User;
import com.mam.vm.repositories.ProductRepository;
import com.mam.vm.repositories.UserRepository;
import com.mam.vm.repositories.rest.DTOs.request.BuyRequest;
import com.mam.vm.repositories.rest.DTOs.request.DepositRequest;
import com.mam.vm.repositories.rest.DTOs.request.LoginRequest;
import com.mam.vm.repositories.rest.DTOs.request.ProductCreateRequest;
import com.mam.vm.repositories.rest.DTOs.request.ProductUpdateRequest;
import com.mam.vm.repositories.rest.DTOs.request.UserCreateRequest;
import com.mam.vm.repositories.rest.DTOs.response.ProductResponse;
import com.mam.vm.security.jwt.JwtService;

import jakarta.transaction.Transactional;




@SpringBootTest
@Transactional
class VendingMachineBackendApplicationTests {


    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    private MockMvc mockMvc;
    private String buyerToken;
    private String sellerToken;
    private String adminToken;
    private User buyer;
    private User seller;
    private User admin;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        // Create test users
        buyer = User.builder()
                .username("buyer")
                .password(passwordEncoder.encode("password"))
                .role(Role.BUYER)
                .deposit(0)
                .build();
        buyer = userRepository.save(buyer);

        seller = User.builder()
                .username("seller")
                .password(passwordEncoder.encode("password"))
                .role(Role.SELLER)
                .deposit(0)
                .build();
        seller = userRepository.save(seller);

//        admin = User.builder()
//                .username("admin")
//                .password(passwordEncoder.encode("password"))
//                .role(Role.ADMIN)
//                .deposit(0)
//                .build();
//        admin = userRepository.save(admin);

        // Generate tokens
        buyerToken = jwtService.generateToken(buyer);
        sellerToken = jwtService.generateToken(seller);
//        adminToken = jwtService.generateToken(admin);
    }

    @Test
    void testUserRegistration() throws Exception {
        UserCreateRequest request = UserCreateRequest.builder()
                .username("newuser")
                .password("password123")
                .role(Role.BUYER)
                .build();

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("newuser"))
                .andExpect(jsonPath("$.role").value("BUYER"));
    }

    @Test
    void testUserLogin() throws Exception {
        LoginRequest request = LoginRequest.builder()
                .username("buyer")
                .password("password")
                .build();

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.user.username").value("buyer"));
    }

//    @Test
//    void testGetAllUsers() throws Exception {
//        mockMvc.perform(get("/api/users")
//                .header("Authorization", "Bearer " + adminToken))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.content").isArray());
//    }

    @Test
    void testGetUserById() throws Exception {
        mockMvc.perform(get("/api/users/" + buyer.getId())
                .header("Authorization", "Bearer " + buyerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("buyer"));
    }

    @Test
    void testCreateProduct() throws Exception {
        ProductCreateRequest request = ProductCreateRequest.builder()
                .productName("Coca Cola")
                .cost(150)
                .amountAvailable(10)
                .build();

        mockMvc.perform(post("/api/products")
                .header("Authorization", "Bearer " + sellerToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.productName").value("Coca Cola"))
                .andExpect(jsonPath("$.cost").value(150))
                .andExpect(jsonPath("$.amountAvailable").value(10));
    }

    @Test
    void testGetAllProducts() throws Exception {
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    void testDepositCoins() throws Exception {
        DepositRequest request = DepositRequest.builder()
                .coin(50)
                .build();

        mockMvc.perform(post("/api/vending/deposit")
                .header("Authorization", "Bearer " + buyerToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currentDeposit").value(50));
    }

    @Test
    void testDepositInvalidCoin() throws Exception {
        DepositRequest request = DepositRequest.builder()
                .coin(25) // Invalid coin value
                .build();

        mockMvc.perform(post("/api/vending/deposit")
                .header("Authorization", "Bearer " + buyerToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetBalance() throws Exception {
        // Since getBalance is not in VendingMachineService, we get balance through user endpoint
        mockMvc.perform(get("/api/users/me")
                .header("Authorization", "Bearer " + buyerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.deposit").exists());
    }

    @Test
    void testResetDeposit() throws Exception {
        // First deposit some coins
        DepositRequest depositRequest = DepositRequest.builder()
                .coin(100)
                .build();

        mockMvc.perform(post("/api/vending/deposit")
                .header("Authorization", "Bearer " + buyerToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(depositRequest)));

        // Then reset
        mockMvc.perform(post("/api/vending/reset")
                .header("Authorization", "Bearer " + buyerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currentDeposit").exists()); // Reset returns DepositResponse
    }

    @Test
    void testBuyProductSuccessfully() throws Exception {
        // Create a product first
        ProductCreateRequest productRequest = ProductCreateRequest.builder()
                .productName("Snickers")
                .cost(100)
                .amountAvailable(5)
                .build();

        String productResponse = mockMvc.perform(post("/api/products")
                .header("Authorization", "Bearer " + sellerToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productRequest)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        ProductResponse product = objectMapper.readValue(productResponse, ProductResponse.class);

        // Deposit coins
        DepositRequest depositRequest = DepositRequest.builder()
                .coin(100)
                .build();

        mockMvc.perform(post("/api/vending/deposit")
                .header("Authorization", "Bearer " + buyerToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(depositRequest)));

        // Buy the product
        BuyRequest buyRequest = BuyRequest.builder()
                .productId(product.getId())
                .amount(1)
                .build();

        mockMvc.perform(post("/api/vending/buy")
                .header("Authorization", "Bearer " + buyerToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(buyRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalSpent").value(100));
    }

    @Test
    void testBuyProductInsufficientFunds() throws Exception {
        // Create a product first
        ProductCreateRequest productRequest = ProductCreateRequest.builder()
                .productName("Expensive Item")
                .cost(500)
                .amountAvailable(5)
                .build();

        String productResponse = mockMvc.perform(post("/api/products")
                .header("Authorization", "Bearer " + sellerToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productRequest)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        ProductResponse product = objectMapper.readValue(productResponse, ProductResponse.class);

        // Deposit insufficient coins
        DepositRequest depositRequest = DepositRequest.builder()
                .coin(100)
                .build();

        mockMvc.perform(post("/api/vending/deposit")
                .header("Authorization", "Bearer " + buyerToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(depositRequest)));

        // Try to buy the product
        BuyRequest buyRequest = BuyRequest.builder()
                .productId(product.getId())
                .amount(1)
                .build();

        mockMvc.perform(post("/api/vending/buy")
                .header("Authorization", "Bearer " + buyerToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(buyRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testSellerAccessControl() throws Exception {
        // Buyer should not be able to create products
        ProductCreateRequest request = ProductCreateRequest.builder()
                .productName("Unauthorized Product")
                .cost(100)
                .amountAvailable(5)
                .build();

        mockMvc.perform(post("/api/products")
                .header("Authorization", "Bearer " + buyerToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    void testBuyerAccessControl() throws Exception {
        // Seller should not be able to deposit coins
        DepositRequest request = DepositRequest.builder()
                .coin(50)
                .build();

        mockMvc.perform(post("/api/vending/deposit")
                .header("Authorization", "Bearer " + sellerToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    void testUpdateProduct() throws Exception {
        // Create a product first
        ProductCreateRequest createRequest = ProductCreateRequest.builder()
                .productName("Original Product")
                .cost(100)
                .amountAvailable(5)
                .build();

        String createResponse = mockMvc.perform(post("/api/products")
                .header("Authorization", "Bearer " + sellerToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        ProductResponse product = objectMapper.readValue(createResponse, ProductResponse.class);

        // Update the product
        ProductUpdateRequest updateRequest = ProductUpdateRequest.builder()
                .productName("Updated Product")
                .cost(150)
                .amountAvailable(10)
                .build();
        
        mockMvc.perform(put("/api/products/" + product.getId())
                .header("Authorization", "Bearer " + sellerToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productName").value("Updated Product"))
                .andExpect(jsonPath("$.cost").value(150));
    }

    @Test
    void testDeleteProduct() throws Exception {
        // Create a product first
        ProductCreateRequest createRequest = ProductCreateRequest.builder()
                .productName("Product to Delete")
                .cost(100)
                .amountAvailable(5)
                .build();

        String createResponse = mockMvc.perform(post("/api/products")
                .header("Authorization", "Bearer " + sellerToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        ProductResponse product = objectMapper.readValue(createResponse, ProductResponse.class);

        // Delete the product
        mockMvc.perform(delete("/api/products/" + product.getId())
                .header("Authorization", "Bearer " + sellerToken))
                .andExpect(status().isNoContent());

        // Verify product is deleted
        mockMvc.perform(get("/api/products/" + product.getId()))
                .andExpect(status().isNotFound());
    }


}
