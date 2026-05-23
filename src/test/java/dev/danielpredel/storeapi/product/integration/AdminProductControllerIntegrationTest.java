package dev.danielpredel.storeapi.product.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.danielpredel.storeapi.product.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Testcontainers
@ActiveProfiles("test")
class AdminProductControllerIntegrationTest {

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
    @Autowired ProductRepository productRepository;

    // Injected from application-test.yaml → sourced from .env exports
    @Value("${app.admin.email}")    private String adminEmail;
    @Value("${app.admin.password}") private String adminPassword;

    private final ObjectMapper objectMapper = new ObjectMapper();

    // ------------------------------------------------------------------
    // Constants
    // ------------------------------------------------------------------

    private static final String BASE_URL = "/admin/products";
    private static final String LOGIN_URL = "/auth/login";

    // ------------------------------------------------------------------
    // Helpers
    // ------------------------------------------------------------------

    private String json(Object obj) throws Exception {
        return objectMapper.writeValueAsString(obj);
    }

    /**
     * Logs in with the seeded admin credentials and returns the Bearer token.
     * The admin user is created by the CommandLineRunner on context startup.
     */
    private String adminToken() throws Exception {
        String body = mockMvc.perform(post(LOGIN_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of(
                                "email",    adminEmail,
                                "password", adminPassword
                        ))))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return objectMapper.readTree(body)
                .path("data").path("token").asText();
    }

    private Map<String, Object> validProductBody() {
        return Map.of(
                "name",     "Test Product",
                "price",    "19.99",
                "stock",    100,
                "imageUrl", "https://img.test/product.jpg",
                "active",   true
        );
    }

    /** Creates a product and returns its id. */
    private Long createProduct(String token) throws Exception {
        String body = mockMvc.perform(post(BASE_URL)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(validProductBody())))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return objectMapper.readTree(body).path("data").path("id").asLong();
    }

    @BeforeEach
    void cleanProducts() {
        // Admin user is preserved — only wipe products between tests
        productRepository.deleteAll();
    }

    // ==================================================================
    // POST /admin/products
    // ==================================================================

    @Test
    void shouldCreateProductAndReturn201WithLocationHeader() throws Exception {
        String token = adminToken();

        mockMvc.perform(post(BASE_URL)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(validProductBody())))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString("/admin/products/")))
                .andExpect(jsonPath("$.message").value("Product created successfully"))
                .andExpect(jsonPath("$.data.id").isNumber())
                .andExpect(jsonPath("$.data.name").value("Test Product"))
                .andExpect(jsonPath("$.data.price").value(19.99))
                .andExpect(jsonPath("$.data.stock").value(100))
                .andExpect(jsonPath("$.data.active").value(true));
    }

    @Test
    void shouldReturn401WhenCreatingProductWithoutToken() throws Exception {
        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(validProductBody())))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturn403WhenRegularUserTriesToCreateProduct() throws Exception {
        // Register a regular USER and obtain their token
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of(
                                "name",     "Regular User",
                                "email",    "user@mail.com",
                                "password", "PASSword@@12",
                                "address",  "Some Address"
                        ))))
                .andExpect(status().isCreated());

        String userBody = mockMvc.perform(post(LOGIN_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of(
                                "email",    "user@mail.com",
                                "password", "PASSword@@12"
                        ))))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        String userToken = objectMapper.readTree(userBody)
                .path("data").path("token").asText();

        mockMvc.perform(post(BASE_URL)
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(validProductBody())))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturn400WhenProductNameIsMissing() throws Exception {
        String token = adminToken();
        var body = Map.of(
                "price",    "19.99",
                "stock",    100,
                "imageUrl", "https://img.test/product.jpg",
                "active",   true
        );

        mockMvc.perform(post(BASE_URL)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(body)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400WhenPriceIsNegative() throws Exception {
        String token = adminToken();
        var body = Map.of(
                "name",     "Bad Product",
                "price",    "-5.00",
                "stock",    10,
                "imageUrl", "https://img.test/product.jpg",
                "active",   true
        );

        mockMvc.perform(post(BASE_URL)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(body)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400WhenStockIsNegative() throws Exception {
        String token = adminToken();
        var body = Map.of(
                "name",     "Bad Product",
                "price",    "9.99",
                "stock",    -1,
                "imageUrl", "https://img.test/product.jpg",
                "active",   true
        );

        mockMvc.perform(post(BASE_URL)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(body)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400WhenImageUrlIsInvalid() throws Exception {
        String token = adminToken();
        var body = Map.of(
                "name",     "Bad Product",
                "price",    "9.99",
                "stock",    10,
                "imageUrl", "not-a-url",
                "active",   true
        );

        mockMvc.perform(post(BASE_URL)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(body)))
                .andExpect(status().isBadRequest());
    }

    // ==================================================================
    // GET /admin/products
    // ==================================================================

    @Test
    void shouldReturnPagedProductsForAdmin() throws Exception {
        String token = adminToken();
        createProduct(token);
        createProduct(token);

        mockMvc.perform(get(BASE_URL)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Products retrieved successfully"))
                .andExpect(jsonPath("$.data.content", hasSize(2)));
    }

    @Test
    void shouldReturnEmptyPageWhenNoProductsExist() throws Exception {
        String token = adminToken();

        mockMvc.perform(get(BASE_URL)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content", hasSize(0)));
    }

    @Test
    void shouldReturn401WhenListingProductsWithoutToken() throws Exception {
        mockMvc.perform(get(BASE_URL))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturn400WhenPageSizeExceedsMaximum() throws Exception {
        String token = adminToken();

        mockMvc.perform(get(BASE_URL)
                        .header("Authorization", "Bearer " + token)
                        .param("size", "100"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldIgnoreInvalidSortFieldAndFallBackToId() throws Exception {
        String token = adminToken();
        createProduct(token);

        mockMvc.perform(get(BASE_URL)
                        .header("Authorization", "Bearer " + token)
                        .param("sortBy", "hackerField"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content", hasSize(1)));
    }

    // ==================================================================
    // GET /admin/products/{id}
    // ==================================================================

    @Test
    void shouldReturnProductById() throws Exception {
        String token = adminToken();
        Long id = createProduct(token);

        mockMvc.perform(get(BASE_URL + "/" + id)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Product retrieved successfully"))
                .andExpect(jsonPath("$.data.id").value(id))
                .andExpect(jsonPath("$.data.name").value("Test Product"));
    }

    @Test
    void shouldReturn404WhenProductDoesNotExist() throws Exception {
        String token = adminToken();

        mockMvc.perform(get(BASE_URL + "/999999")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturn401WhenFetchingProductWithoutToken() throws Exception {
        mockMvc.perform(get(BASE_URL + "/1"))
                .andExpect(status().isUnauthorized());
    }

    // ==================================================================
    // PUT /admin/products/{id}
    // ==================================================================

    @Test
    void shouldUpdateProductAndReturn200() throws Exception {
        String token = adminToken();
        Long id = createProduct(token);

        var updated = Map.of(
                "name",     "Updated Product",
                "price",    "29.99",
                "stock",    50,
                "imageUrl", "https://img.test/updated.jpg",
                "active",   false
        );

        mockMvc.perform(put(BASE_URL + "/" + id)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Product updated successfully"))
                .andExpect(jsonPath("$.data.name").value("Updated Product"))
                .andExpect(jsonPath("$.data.price").value(29.99))
                .andExpect(jsonPath("$.data.stock").value(50))
                .andExpect(jsonPath("$.data.active").value(false));
    }

    @Test
    void shouldReturn404WhenUpdatingNonExistentProduct() throws Exception {
        String token = adminToken();

        mockMvc.perform(put(BASE_URL + "/999999")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(validProductBody())))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturn400WhenUpdatePayloadIsInvalid() throws Exception {
        String token = adminToken();
        Long id = createProduct(token);

        var bad = Map.of(
                "name",  "",        // blank — fails @NotBlank
                "price", "-1.00",   // negative — fails @Positive
                "stock", -5,        // negative — fails @PositiveOrZero
                "active", true
        );

        mockMvc.perform(put(BASE_URL + "/" + id)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(bad)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn401WhenUpdatingProductWithoutToken() throws Exception {
        mockMvc.perform(put(BASE_URL + "/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(validProductBody())))
                .andExpect(status().isUnauthorized());
    }
}
