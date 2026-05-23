package dev.danielpredel.storeapi;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@OpenAPIDefinition(
        info = @Info(
                title = "Store API",
                version = "1.0",
                description = "API documentation for managing products, users and orders"
        )
)
@SpringBootApplication
public class StoreApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(StoreApiApplication.class, args);
    }
}
