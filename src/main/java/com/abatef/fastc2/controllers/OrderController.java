package com.abatef.fastc2.controllers;

import com.abatef.fastc2.dtos.drug.DrugOrderDto;
import com.abatef.fastc2.dtos.drug.OrderCreationRequest;
import com.abatef.fastc2.enums.OrderStatus;
import com.abatef.fastc2.models.User;
import com.abatef.fastc2.services.PharmacyService;

import io.swagger.v3.oas.annotations.Operation;

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

    @Operation(summary = "Create a new Order")
    @PostMapping("/")
    public ResponseEntity<DrugOrderDto> createNewOrder(
            @RequestParam(value = "pharmacy_id") Integer pharmacyId,
            @RequestBody OrderCreationRequest request,
            @AuthenticationPrincipal User user) {
        DrugOrderDto order = pharmacyService.orderDrug(request, pharmacyId, user);
        return ResponseEntity.ok(order);
    }

    @Operation(summary = "Get all orders with filters pharmacy_id, drug_id, user_id")
    @GetMapping("/all")
    public ResponseEntity<List<DrugOrderDto>> filterOrders(
            @RequestParam(value = "pharmacy_id") Integer pharmacyId,
            @AuthenticationPrincipal User user,
            @RequestParam(value = "drug_id", required = false) Integer drugId,
            @RequestParam(value = "user_id", required = false) Integer userId,
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "20") Integer size) {
        PageRequest pageable = PageRequest.of(page, size);
        List<DrugOrderDto> orders =
                pharmacyService.getAllOrders(pharmacyId, drugId, userId, pageable, user);
        if (orders.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(orders);
    }

    @Operation(summary = "Get Order By its Id")
    @GetMapping("/order/{id}")
    public ResponseEntity<DrugOrderDto> getOrder(
            @PathVariable Integer id, @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(pharmacyService.getOrderById(id, user));
    }

    @Operation(summary = "Update Order Status")
    @PatchMapping("/status")
    public ResponseEntity<DrugOrderDto> updateOrderStatus(
            @RequestParam(value = "pharmacy_id") Integer pharmacyId,
            @RequestParam(value = "order_id") Integer orderId,
            @RequestParam(value = "status") OrderStatus status,
            @AuthenticationPrincipal User user) {
        DrugOrderDto order = pharmacyService.changeOrderStatus(pharmacyId, orderId, status, user);
        return ResponseEntity.ok(order);
    }

    @Operation(summary = "Cancel an Order")
    @PatchMapping("/cancel")
    public ResponseEntity<DrugOrderDto> cancelOrder(
            @RequestParam(value = "pharmacy_id") Integer pharmacyId,
            @RequestParam(value = "order_id") Integer orderId,
            @AuthenticationPrincipal User user) {
        DrugOrderDto order =
                pharmacyService.changeOrderStatus(pharmacyId, orderId, OrderStatus.CANCELLED, user);
        return ResponseEntity.ok(order);
    }

    @Operation(summary = "Approve an Order")
    @PatchMapping("/approve")
    public ResponseEntity<DrugOrderDto> approveOrder(
            @RequestParam(value = "pharmacy_id") Integer pharmacyId,
            @RequestParam(value = "order_id") Integer orderId,
            @AuthenticationPrincipal User user) {
        DrugOrderDto order =
                pharmacyService.changeOrderStatus(pharmacyId, orderId, OrderStatus.COMPLETED, user);
        return ResponseEntity.ok(order);
    }
}
