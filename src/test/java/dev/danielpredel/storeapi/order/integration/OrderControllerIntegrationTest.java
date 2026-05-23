package dev.danielpredel.storeapi.order.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.danielpredel.storeapi.order.repository.OrderRepository;
import dev.danielpredel.storeapi.product.entity.Product;
import dev.danielpredel.storeapi.product.repository.ProductRepository;
import dev.danielpredel.storeapi.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Testcontainers
@ActiveProfiles("test")
class OrderControllerIntegrationTest {

    @Container
    static final PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:16-alpine")
                    .withDatabaseName("storeapi_test")
                    .withUsername("test")
                    .withPassword("test");

    @DynamicPropertySource
    static void configureDataSource(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url",      postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired MockMvc mockMvc;
    @Autowired UserRepository userRepository;
    @Autowired OrderRepository orderRepository;
    @Autowired ProductRepository productRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    // ------------------------------------------------------------------
    // Constants
    // ------------------------------------------------------------------

    private static final String ORDERS_URL  = "/orders";
    private static final String PREVIEW_URL = "/orders/preview";

    private static final String REGISTER_URL = "/auth/register";
    private static final String LOGIN_URL    = "/auth/login";

    private static final String VALID_NAME     = "John Doe";
    private static final String VALID_EMAIL    = "order-test@mail.com";
    private static final String VALID_PASSWORD = "PASSword@@12";
    private static final String VALID_ADDRESS  = "Test Address";

    // ------------------------------------------------------------------
    // Helpers
    // ------------------------------------------------------------------

    private String json(Object obj) throws Exception {
        return objectMapper.writeValueAsString(obj);
    }

    /**
     * Registers a user and returns the Bearer token obtained from login.
     * All order endpoints require ROLE_USER, so every test that hits a
     * protected endpoint calls this first.
     */
    private String registerAndLogin() throws Exception {
        mockMvc.perform(post(REGISTER_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of(
                                "name",     VALID_NAME,
                                "email",    VALID_EMAIL,
                                "password", VALID_PASSWORD,
                                "address",  VALID_ADDRESS
                        ))))
                .andExpect(status().isCreated());

        String body = mockMvc.perform(post(LOGIN_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of(
                                "email",    VALID_EMAIL,
                                "password", VALID_PASSWORD
                        ))))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // Extract token from: {"data":{"token":"..."}, ...}
        return objectMapper.readTree(body)
                .path("data").path("token").asText();
    }

    /** Saves a product directly via repository and returns it. */
    private Product createProduct(String name, BigDecimal price, int stock) {
        return productRepository.save(new Product(name, price, stock, "http://img.test/p.jpg", true));
    }

    /** Builds the JSON body for POST /orders with a single item. */
    private String singleItemOrderBody(Long productId, int quantity) throws Exception {
        return json(Map.of("items", List.of(
                Map.of("productId", productId, "quantity", quantity)
        )));
    }

    /** Posts an order and returns its id. */
    private Long createOrder(String token, Long productId, int quantity) throws Exception {
        String body = mockMvc.perform(post(ORDERS_URL)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(singleItemOrderBody(productId, quantity)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return objectMapper.readTree(body).path("data").path("id").asLong();
    }

    @BeforeEach
    void cleanDatabase() {
        orderRepository.deleteAll();
        productRepository.deleteAll();
        userRepository.deleteAll();
    }

    // ==================================================================
    // POST /orders
    // ==================================================================

    @Test
    void shouldCreateOrderAndReturn201WithLocationHeader() throws Exception {
        String token = registerAndLogin();
        Product product = createProduct("Widget", new BigDecimal("9.99"), 10);

        mockMvc.perform(post(ORDERS_URL)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(singleItemOrderBody(product.getId(), 2)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString("/orders/")))
                .andExpect(jsonPath("$.message").value("Order created successfully"))
                .andExpect(jsonPath("$.data.id").isNumber())
                .andExpect(jsonPath("$.data.status").value("CREATED"))
                .andExpect(jsonPath("$.data.totalAmount").value(19.98))
                .andExpect(jsonPath("$.data.orderItems", hasSize(1)));
    }

    @Test
    void shouldReturn401WhenCreatingOrderWithoutToken() throws Exception {
        Product product = createProduct("Widget", new BigDecimal("9.99"), 10);

        mockMvc.perform(post(ORDERS_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(singleItemOrderBody(product.getId(), 1)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturn400WhenOrderItemsIsEmpty() throws Exception {
        String token = registerAndLogin();

        mockMvc.perform(post(ORDERS_URL)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("items", List.of()))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400WhenOrderItemQuantityIsZero() throws Exception {
        String token = registerAndLogin();
        Product product = createProduct("Widget", new BigDecimal("9.99"), 10);

        mockMvc.perform(post(ORDERS_URL)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(singleItemOrderBody(product.getId(), 0)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn404WhenProductDoesNotExist() throws Exception {
        String token = registerAndLogin();

        mockMvc.perform(post(ORDERS_URL)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(singleItemOrderBody(999L, 1)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturn409WhenStockIsInsufficient() throws Exception {
        String token = registerAndLogin();
        Product product = createProduct("Widget", new BigDecimal("9.99"), 2);

        mockMvc.perform(post(ORDERS_URL)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(singleItemOrderBody(product.getId(), 5)))
                .andExpect(status().isConflict());
    }

    @Test
    void shouldDecrementProductStockAfterOrderCreation() throws Exception {
        String token = registerAndLogin();
        Product product = createProduct("Widget", new BigDecimal("9.99"), 10);

        createOrder(token, product.getId(), 3);

        Product updated = productRepository.findById(product.getId()).orElseThrow();
        assert updated.getStock() == 7;
    }

    // ==================================================================
    // GET /orders
    // ==================================================================

    @Test
    void shouldReturnPagedOrdersForAuthenticatedUser() throws Exception {
        String token = registerAndLogin();
        Product product = createProduct("Widget", new BigDecimal("9.99"), 10);
        createOrder(token, product.getId(), 1);

        mockMvc.perform(get(ORDERS_URL)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Orders retrieved successfully"))
                .andExpect(jsonPath("$.data.content", hasSize(1)))
                .andExpect(jsonPath("$.data.content[0].status").value("CREATED"));
    }

    @Test
    void shouldReturnEmptyPageWhenUserHasNoOrders() throws Exception {
        String token = registerAndLogin();

        mockMvc.perform(get(ORDERS_URL)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content", hasSize(0)));
    }

    @Test
    void shouldReturn401WhenListingOrdersWithoutToken() throws Exception {
        mockMvc.perform(get(ORDERS_URL))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturn400WhenPageSizeExceedsMaximum() throws Exception {
        String token = registerAndLogin();

        mockMvc.perform(get(ORDERS_URL)
                        .header("Authorization", "Bearer " + token)
                        .param("size", "100"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldIgnoreInvalidSortFieldAndFallBackToId() throws Exception {
        String token = registerAndLogin();
        Product product = createProduct("Widget", new BigDecimal("9.99"), 10);
        createOrder(token, product.getId(), 1);

        // Invalid sortBy value — controller silently falls back to "id"
        mockMvc.perform(get(ORDERS_URL)
                        .header("Authorization", "Bearer " + token)
                        .param("sortBy", "hackerField"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content", hasSize(1)));
    }

    // ==================================================================
    // GET /orders/{id}
    // ==================================================================

    @Test
    void shouldReturnOrderByIdForOwner() throws Exception {
        String token = registerAndLogin();
        Product product = createProduct("Widget", new BigDecimal("9.99"), 10);
        Long orderId = createOrder(token, product.getId(), 1);

        mockMvc.perform(get(ORDERS_URL + "/" + orderId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Order retrieved successfully"))
                .andExpect(jsonPath("$.data.id").value(orderId));
    }

    @Test
    void shouldReturn404WhenOrderDoesNotExist() throws Exception {
        String token = registerAndLogin();

        mockMvc.perform(get(ORDERS_URL + "/999999")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturn401WhenFetchingOrderWithoutToken() throws Exception {
        mockMvc.perform(get(ORDERS_URL + "/1"))
                .andExpect(status().isUnauthorized());
    }

    // ==================================================================
    // PATCH /orders/{id}/cancel
    // ==================================================================

    @Test
    void shouldCancelOrderAndReturn200() throws Exception {
        String token = registerAndLogin();
        Product product = createProduct("Widget", new BigDecimal("9.99"), 10);
        Long orderId = createOrder(token, product.getId(), 2);

        mockMvc.perform(patch(ORDERS_URL + "/" + orderId + "/cancel")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Order canceled successfully"))
                .andExpect(jsonPath("$.data.status").value("CANCELLED"));
    }

    @Test
    void shouldRestoreStockAfterCancellation() throws Exception {
        String token = registerAndLogin();
        Product product = createProduct("Widget", new BigDecimal("9.99"), 10);
        Long orderId = createOrder(token, product.getId(), 3);

        mockMvc.perform(patch(ORDERS_URL + "/" + orderId + "/cancel")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        Product updated = productRepository.findById(product.getId()).orElseThrow();
        assert updated.getStock() == 10;
    }

    @Test
    void shouldReturn409WhenCancellingAlreadyCancelledOrder() throws Exception {
        String token = registerAndLogin();
        Product product = createProduct("Widget", new BigDecimal("9.99"), 10);
        Long orderId = createOrder(token, product.getId(), 1);

        // Cancel once
        mockMvc.perform(patch(ORDERS_URL + "/" + orderId + "/cancel")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        // Cancel again — InvalidOrderStateException
        mockMvc.perform(patch(ORDERS_URL + "/" + orderId + "/cancel")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isConflict());
    }

    @Test
    void shouldReturn404WhenCancellingNonExistentOrder() throws Exception {
        String token = registerAndLogin();

        mockMvc.perform(patch(ORDERS_URL + "/999999/cancel")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturn401WhenCancellingOrderWithoutToken() throws Exception {
        mockMvc.perform(patch(ORDERS_URL + "/1/cancel"))
                .andExpect(status().isUnauthorized());
    }

    // ==================================================================
    // POST /orders/preview
    // ==================================================================

    @Test
    void shouldReturnPreviewWithTotalsForValidItems() throws Exception {
        String token = registerAndLogin();
        Product product = createProduct("Widget", new BigDecimal("9.99"), 10);

        mockMvc.perform(post(PREVIEW_URL)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(singleItemOrderBody(product.getId(), 3)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Order preview retrieved successfully"))
                .andExpect(jsonPath("$.data.totalAmount").value(29.97))
                .andExpect(jsonPath("$.data.orderItems", hasSize(1)))
                .andExpect(jsonPath("$.data.orderItems[0].availableStock").value(10))
                .andExpect(jsonPath("$.data.orderItems[0].requestedQuantity").value(3));
    }

    @Test
    void shouldReturn404WhenPreviewContainsUnknownProduct() throws Exception {
        String token = registerAndLogin();

        mockMvc.perform(post(PREVIEW_URL)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(singleItemOrderBody(999L, 1)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturn400WhenPreviewItemsIsEmpty() throws Exception {
        String token = registerAndLogin();

        mockMvc.perform(post(PREVIEW_URL)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("items", List.of()))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn401WhenPreviewRequestHasNoToken() throws Exception {
        Product product = createProduct("Widget", new BigDecimal("9.99"), 10);

        mockMvc.perform(post(PREVIEW_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(singleItemOrderBody(product.getId(), 1)))
                .andExpect(status().isUnauthorized());
    }
}
