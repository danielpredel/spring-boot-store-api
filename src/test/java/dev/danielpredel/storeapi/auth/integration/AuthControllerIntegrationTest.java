package dev.danielpredel.storeapi.auth.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.danielpredel.storeapi.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Testcontainers
@ActiveProfiles("test")
class AuthControllerIntegrationTest {

    // Single shared container — started once for the entire test class.
    @Container
    static final PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:16-alpine")
                    .withDatabaseName("storeapi_test")
                    .withUsername("test")
                    .withPassword("test");

    // Feed container coordinates into Spring before the context starts.
    @DynamicPropertySource
    static void configureDataSource(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url",      postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired MockMvc mockMvc;
    @Autowired UserRepository userRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    // ------------------------------------------------------------------
    // Helpers
    // ------------------------------------------------------------------

    private static final String REGISTER_URL = "/auth/register";
    private static final String LOGIN_URL    = "/auth/login";

    private static final String VALID_NAME     = "John Doe";
    private static final String VALID_EMAIL    = "test@mail.com";
    private static final String VALID_PASSWORD = "PASSword@@12";
    private static final String VALID_ADDRESS  = "Test Address";

    private Map<String, String> registerBody() {
        return Map.of(
                "name",     VALID_NAME,
                "email",    VALID_EMAIL,
                "password", VALID_PASSWORD,
                "address",  VALID_ADDRESS
        );
    }

    private Map<String, String> loginBody() {
        return Map.of("email", VALID_EMAIL, "password", VALID_PASSWORD);
    }

    private String json(Object obj) throws Exception {
        return objectMapper.writeValueAsString(obj);
    }

    /** Register a user and return; used as setup for login tests. */
    private void registerUser() throws Exception {
        mockMvc.perform(post(REGISTER_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(registerBody())))
                .andExpect(status().isCreated());
    }

    @BeforeEach
    void cleanDatabase() {
        // Start each test with a clean slate so tests are order-independent.
        userRepository.deleteAll();
    }

    // ==================================================================
    // POST /auth/register
    // ==================================================================

    @Test
    void shouldRegisterUserAndReturn201WithLocationHeader() throws Exception {
        mockMvc.perform(post(REGISTER_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(registerBody())))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString("/users/")))
                .andExpect(jsonPath("$.message").value("User registered successfully"))
                // status in the body should be 200
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.data.id").isNumber());
    }

    @Test
    void shouldReturn409WhenEmailAlreadyExists() throws Exception {
        registerUser(); // first registration succeeds

        mockMvc.perform(post(REGISTER_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(registerBody())))
                // EmailAlreadyExistsException should map to 409 via exception handler
                .andExpect(status().isConflict());
    }

    @Test
    void shouldReturn400WhenNameIsMissing() throws Exception {
        var body = Map.of(
                "email",    VALID_EMAIL,
                "password", VALID_PASSWORD,
                "address",  VALID_ADDRESS
        );

        mockMvc.perform(post(REGISTER_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(body)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400WhenEmailIsInvalid() throws Exception {
        var body = Map.of(
                "name",     VALID_NAME,
                "email",    "not-an-email",
                "password", VALID_PASSWORD,
                "address",  VALID_ADDRESS
        );

        mockMvc.perform(post(REGISTER_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(body)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400WhenPasswordIsTooWeak() throws Exception {
        // Fails the regex: no symbols, only 1 uppercase, etc.
        var body = Map.of(
                "name",     VALID_NAME,
                "email",    VALID_EMAIL,
                "password", "weakpassword",
                "address",  VALID_ADDRESS
        );

        mockMvc.perform(post(REGISTER_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(body)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400WhenAddressIsMissing() throws Exception {
        var body = Map.of(
                "name",     VALID_NAME,
                "email",    VALID_EMAIL,
                "password", VALID_PASSWORD
        );

        mockMvc.perform(post(REGISTER_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(body)))
                .andExpect(status().isBadRequest());
    }

    // ==================================================================
    // POST /auth/login
    // ==================================================================

    @Test
    void shouldLoginAndReturn200WithToken() throws Exception {
        registerUser();

        mockMvc.perform(post(LOGIN_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(loginBody())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Login successful"))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                // JWT is a non-blank string
                .andExpect(jsonPath("$.data.token").isString())
                .andExpect(jsonPath("$.data.token").isNotEmpty());
    }

    @Test
    void shouldReturn401WhenPasswordIsWrong() throws Exception {
        registerUser();

        var body = Map.of("email", VALID_EMAIL, "password", "WrongPass@@99");

        mockMvc.perform(post(LOGIN_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(body)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturn404WhenEmailIsUnknown() throws Exception {
        var body = Map.of("email", "ghost@example.com", "password", VALID_PASSWORD);

        mockMvc.perform(post(LOGIN_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(body)))
                // exception handler maps UsernameNotFoundException → 404
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturn400WhenLoginEmailIsMissing() throws Exception {
        var body = Map.of("password", VALID_PASSWORD);

        mockMvc.perform(post(LOGIN_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(body)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400WhenLoginPasswordIsMissing() throws Exception {
        var body = Map.of("email", VALID_EMAIL);

        mockMvc.perform(post(LOGIN_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(body)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400WhenLoginEmailFormatIsInvalid() throws Exception {
        var body = Map.of("email", "bad-email", "password", VALID_PASSWORD);

        mockMvc.perform(post(LOGIN_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(body)))
                .andExpect(status().isBadRequest());
    }
}
