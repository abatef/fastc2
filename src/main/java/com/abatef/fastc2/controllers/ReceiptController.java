package com.abatef.fastc2.controllers;

import com.abatef.fastc2.dtos.receipt.ReceiptCreationRequest;
import com.abatef.fastc2.dtos.receipt.ReceiptInfo;
import com.abatef.fastc2.models.User;
import com.abatef.fastc2.services.ReceiptService;

import org.springframework.data.domain.PageRequest;
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

    @PostMapping("/")
    public ResponseEntity<ReceiptInfo> createANewReceipt(
            @RequestBody ReceiptCreationRequest request, @AuthenticationPrincipal User user) {
        ReceiptInfo info = receiptService.createANewReceipt(request, user);
        return ResponseEntity.ok(info);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReceiptInfo> getReceiptById(@PathVariable("id") Integer id) {
        ReceiptInfo info = receiptService.getReceiptInfoById(id);
        return ResponseEntity.ok(info);
    }

    private ResponseEntity<List<ReceiptInfo>> noContentOrReturn(List<ReceiptInfo> receipts) {
        if (receipts.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(receipts);
    }

    @GetMapping("/drug/{id}")
    public ResponseEntity<List<ReceiptInfo>> getReceiptsByDrugId(
            @PathVariable Integer id,
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size) {
        List<ReceiptInfo> receipts =
                receiptService.getReceiptsByDrugId(id, PageRequest.of(page, size));
        return noContentOrReturn(receipts);
    }

    @GetMapping("/cashier/{id}")
    public ResponseEntity<List<ReceiptInfo>> getReceiptsByCashierId(
            @PathVariable Integer id,
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size) {
        List<ReceiptInfo> receipts =
                receiptService.getReceiptsByCashierId(id, PageRequest.of(page, size));
        return noContentOrReturn(receipts);
    }

    @GetMapping("/pharmacy/{id}")
    public ResponseEntity<List<ReceiptInfo>> getReceiptsByPharmacyId(
            @PathVariable Integer id,
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size) {
        List<ReceiptInfo> receipts =
                receiptService.getReceiptsByPharmacyId(id, PageRequest.of(page, size));
        return noContentOrReturn(receipts);
    }

    @GetMapping("/shift/{id}")
    public ResponseEntity<List<ReceiptInfo>> getReceiptsByShiftId(
            @PathVariable Integer id,
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size) {
        List<ReceiptInfo> receipts =
                receiptService.getReceiptsByShiftId(id, PageRequest.of(page, size));
        return noContentOrReturn(receipts);
    }

    @GetMapping("/filter")
    public ResponseEntity<List<ReceiptInfo>> getReceiptsByFilters(
            @RequestParam(value = "cashier_id", required = false) Integer cashierId,
            @RequestParam(value = "drug_id", required = false) Integer drugId,
            @RequestParam(value = "pharmacy_id", required = false) Integer pharmacyId,
            @RequestParam(value = "shift_id", required = false) Integer shiftId,
            @RequestParam(value = "from_date", required = false) LocalDate fromDate,
            @RequestParam(value = "to_date", required = false) LocalDate toDate,
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size) {
        List<ReceiptInfo> receipts = receiptService.applyAllFilters(cashierId, drugId, pharmacyId, shiftId, fromDate, toDate, PageRequest.of(page, size));
        return noContentOrReturn(receipts);
    }
}
