package dev.danielpredel.userapibasic.controller;

import dev.danielpredel.userapibasic.dto.*;
import dev.danielpredel.userapibasic.service.AdminOrderService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/orders")
public class AdminOrderController {
    private final AdminOrderService adminOrderService;

    public AdminOrderController(AdminOrderService adminOrderService) {
        this.adminOrderService = adminOrderService;
    }

    @GetMapping
    public ResponseEntity<Page<AdminOrderResponse>> findAll(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(50) int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction
    ) {
        List<String> allowed = List.of("id", "purchaseDate", "status", "totalAmount", "userId");
        if (!allowed.contains(sortBy)) sortBy = "id";

        Sort sort = Sort.by(
                "desc".equalsIgnoreCase(direction)
                        ? Sort.Direction.DESC
                        : Sort.Direction.ASC,
                sortBy
        );

        Pageable pageable = PageRequest.of(page, size, sort);

        return ResponseEntity.ok(adminOrderService.findAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AdminOrderResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(adminOrderService.findById(id));
    }

    @PatchMapping("/{id}/deliver")
    public ResponseEntity<AdminOrderResponse> deliver(@PathVariable Long id) {
        return ResponseEntity.ok(adminOrderService.deliver(id));
    }
}
