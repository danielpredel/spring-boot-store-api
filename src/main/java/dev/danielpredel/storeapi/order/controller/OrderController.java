package dev.danielpredel.storeapi.order.controller;

import dev.danielpredel.storeapi.common.dto.ApiResponse;
import dev.danielpredel.storeapi.order.dto.OrderPreviewRequest;
import dev.danielpredel.storeapi.order.dto.OrderPreviewResponse;
import dev.danielpredel.storeapi.order.dto.OrderRequest;
import dev.danielpredel.storeapi.order.dto.OrderResponse;
import dev.danielpredel.storeapi.order.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/orders")
@Tag(name = "Orders")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    @Operation(summary = "Create order for current user (USER only)")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ApiResponse<OrderResponse>> create(@Valid @RequestBody OrderRequest dto) {
        OrderResponse orderResponse = orderService.save(dto);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(orderResponse.id())
                .toUri();

        return ResponseEntity.created(location).body(
                new ApiResponse<>(
                        "Order created successfully",
                        HttpStatus.CREATED.value(),
                        Instant.now().toString(),
                        orderResponse
                )
        );
    }

    @GetMapping
    @Operation(summary = "Get current user's orders (USER only)")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ApiResponse<Page<OrderResponse>>> findAll(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(50) int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction
    ) {
        List<String> allowed = List.of("id", "purchaseDate", "status", "totalAmount");
        if (!allowed.contains(sortBy)) sortBy = "id";

        Sort sort = Sort.by(
                "desc".equalsIgnoreCase(direction)
                        ? Sort.Direction.DESC
                        : Sort.Direction.ASC,
                sortBy
        );

        Pageable pageable = PageRequest.of(page, size, sort);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        "Orders retrieved successfully",
                        HttpStatus.OK.value(),
                        Instant.now().toString(),
                        orderService.findAll(pageable)
                )
        );
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get current user's order by id (USER only)")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ApiResponse<OrderResponse>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(
                new ApiResponse<>(
                        "Order retrieved successfully",
                        HttpStatus.OK.value(),
                        Instant.now().toString(),
                        orderService.findById(id)
                )
        );
    }

    @PatchMapping("/{id}/cancel")
    @Operation(summary = "Cancel current user's order (USER only)")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ApiResponse<OrderResponse>> cancel(@PathVariable Long id) {
        return ResponseEntity.ok(
                new ApiResponse<>(
                        "Order canceled successfully",
                        HttpStatus.OK.value(),
                        Instant.now().toString(),
                        orderService.cancel(id)
                )
        );
    }

    @PostMapping("/preview")
    @Operation(summary = "Preview order (USER only)")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ApiResponse<OrderPreviewResponse>> preview(@Valid @RequestBody OrderPreviewRequest dto) {
        return ResponseEntity.ok(
                new ApiResponse<>(
                        "Order preview retrieved successfully",
                        HttpStatus.OK.value(),
                        Instant.now().toString(),
                        orderService.preview(dto)
                )
        );
    }
}
