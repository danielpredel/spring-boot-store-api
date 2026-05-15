package dev.danielpredel.userapibasic.controller;

import dev.danielpredel.userapibasic.dto.OrderPreviewRequest;
import dev.danielpredel.userapibasic.dto.OrderPreviewResponse;
import dev.danielpredel.userapibasic.dto.OrderRequest;
import dev.danielpredel.userapibasic.dto.OrderResponse;
import dev.danielpredel.userapibasic.security.CustomUserDetails;
import dev.danielpredel.userapibasic.service.OrderService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<OrderResponse> create(@AuthenticationPrincipal CustomUserDetails user, @Valid @RequestBody OrderRequest dto) {
        OrderResponse orderResponse = orderService.save(user.getId(), dto);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(orderResponse.id())
                .toUri();

        return ResponseEntity.created(location).body(orderResponse);
    }

    @GetMapping
    public ResponseEntity<Page<OrderResponse>> findAll(
            @AuthenticationPrincipal CustomUserDetails user,
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

        return ResponseEntity.ok(orderService.findAll(user.getId(), pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.findById(id));
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<OrderResponse> cancel(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.cancel(id));
    }

    @PostMapping("/preview")
    public ResponseEntity<OrderPreviewResponse> preview(@Valid @RequestBody OrderPreviewRequest dto) {
        return ResponseEntity.ok(orderService.preview(dto));
    }
}
