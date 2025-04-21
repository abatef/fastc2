package com.abatef.fastc2.controllers;

import com.abatef.fastc2.dtos.receipt.ReceiptCreationRequest;
import com.abatef.fastc2.dtos.receipt.ReceiptInfo;
import com.abatef.fastc2.enums.ReceiptStatus;
import com.abatef.fastc2.models.User;
import com.abatef.fastc2.services.ReceiptService;

import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/receipts")
public class ReceiptController {
    private final ReceiptService receiptService;

    public ReceiptController(ReceiptService receiptService) {
        this.receiptService = receiptService;
    }

    @PostMapping
    public ResponseEntity<ReceiptInfo> createANewReceipt(
            @Valid @RequestBody List<ReceiptCreationRequest> request, @AuthenticationPrincipal User user) {
        ReceiptInfo info = receiptService.createANewReceipt(request, user);
        return ResponseEntity.ok(info);
    }

    @GetMapping("/{receipt_id}")
    public ResponseEntity<ReceiptInfo> getReceiptById(@PathVariable("receipt_id") Integer id) {
        ReceiptInfo info = receiptService.getReceiptInfoById(id);
        return ResponseEntity.ok(info);
    }

    private ResponseEntity<List<ReceiptInfo>> noContentOrReturn(List<ReceiptInfo> receipts) {
        if (receipts.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(receipts);
    }

    @PatchMapping("/{receipt_id}/status")
    public ResponseEntity<ReceiptInfo> updateReceiptStatus(
            @PathVariable("receipt_id") Integer id,
            @RequestParam ReceiptStatus status,
            @AuthenticationPrincipal User user) {
        ReceiptInfo info = receiptService.updateReceiptStatus(id, status, user);
        return ResponseEntity.ok(info);
    }

    @GetMapping("/filter")
    public ResponseEntity<List<ReceiptInfo>> getReceiptsByFilters(
            @RequestParam(value = "cashier_id", required = false) Integer cashierId,
            @RequestParam(value = "drug_id", required = false) Integer drugId,
            @RequestParam(value = "pharmacy_id", required = false) Integer pharmacyId,
            @RequestParam(value = "shift_id", required = false) Integer shiftId,
            @RequestParam(value = "from_date", required = false) LocalDate fromDate,
            @RequestParam(value = "to_date", required = false) LocalDate toDate,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        PageRequest pageable = PageRequest.of(page, size);
        List<ReceiptInfo> receipts =
                receiptService.applyAllFilters(
                        cashierId, drugId, pharmacyId, shiftId, fromDate, toDate, pageable);
        return noContentOrReturn(receipts);
    }
}
