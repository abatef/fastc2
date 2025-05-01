package com.abatef.fastc2.controllers;

import com.abatef.fastc2.dtos.drug.DrugOrderDto;
import com.abatef.fastc2.dtos.drug.OrderItemRequest;
import com.abatef.fastc2.models.User;
import com.abatef.fastc2.services.PharmacyService;

import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {
    private final PharmacyService pharmacyService;

    public OrderController(PharmacyService pharmacyService) {
        this.pharmacyService = pharmacyService;
    }

    @PostMapping("/")
    public ResponseEntity<DrugOrderDto> createNewOrder(
            @RequestParam(value = "pharmacy_id") Integer pharmacyId,
            @RequestBody List<OrderItemRequest> request,
            @AuthenticationPrincipal User user) {
        DrugOrderDto order = pharmacyService.orderDrug(request, pharmacyId, user);
        return ResponseEntity.ok(order);
    }

    @GetMapping("/all")
    public ResponseEntity<List<DrugOrderDto>> filterOrders(
            @RequestParam(value = "pharmacy_id") Integer pharmacyId,
            @RequestParam(value = "drug_id", required = false) Integer drugId,
            @RequestParam(value = "user_id", required = false) Integer userId,
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "20") Integer size) {
        PageRequest pageable = PageRequest.of(page, size);
        List<DrugOrderDto> orders =
                pharmacyService.getAllOrders(pharmacyId, drugId, userId, pageable);
        if (orders.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(orders);
    }

    @PatchMapping("/cancel")
    public ResponseEntity<DrugOrderDto> cancelOrder(
            @RequestParam(value = "pharmacy_id") Integer pharmacyId,
            @RequestParam(value = "order_id") Integer orderId,
            @AuthenticationPrincipal User user) {
        DrugOrderDto order = pharmacyService.cancelOrder(pharmacyId, orderId, user);
        return ResponseEntity.ok(order);
    }

    @PatchMapping("/approve")
    public ResponseEntity<DrugOrderDto> approveOrder(
            @RequestParam(value = "pharmacy_id") Integer pharmacyId,
            @RequestParam(value = "order_id") Integer orderId,
            @AuthenticationPrincipal User user) {
        DrugOrderDto order = pharmacyService.approveOrder(pharmacyId, orderId, user);
        return ResponseEntity.ok(order);
    }
}
