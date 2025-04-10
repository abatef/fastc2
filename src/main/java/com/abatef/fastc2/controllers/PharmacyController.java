package com.abatef.fastc2.controllers;

import com.abatef.fastc2.dtos.drug.PharmacyDrugCreationRequest;
import com.abatef.fastc2.dtos.drug.PharmacyDrugInfo;
import com.abatef.fastc2.dtos.pharmacy.PharmacyCreationRequest;
import com.abatef.fastc2.dtos.pharmacy.PharmacyInfo;
import com.abatef.fastc2.models.PharmacyDrugId;
import com.abatef.fastc2.models.User;
import com.abatef.fastc2.services.PharmacyService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/pharmacy")
public class PharmacyController {
    private final PharmacyService pharmacyService;

    public PharmacyController(PharmacyService pharmacyService) {
        this.pharmacyService = pharmacyService;
    }

    @PostMapping("/")
    public ResponseEntity<PharmacyInfo> createPharmacy(
            @RequestBody PharmacyCreationRequest request, @AuthenticationPrincipal User user) {
        PharmacyInfo info = pharmacyService.createPharmacy(request, user);
        return ResponseEntity.ok(info);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PharmacyInfo> getPharmacyInfo(@PathVariable Integer id) {
        PharmacyInfo info = pharmacyService.getPharmacyInfoById(id);
        return ResponseEntity.ok(info);
    }

    @PostMapping("/add")
    public ResponseEntity<PharmacyDrugInfo> addDrugToPharmacy(
            @RequestBody PharmacyDrugCreationRequest request, @AuthenticationPrincipal User user) {
        PharmacyDrugInfo drug = pharmacyService.addDrugToPharmacy(request, user);
        return ResponseEntity.ok(drug);
    }

    @PatchMapping("/stock")
    public ResponseEntity<PharmacyDrugInfo> updateStock(
            @RequestBody PharmacyDrugId id,
            @RequestParam("quantity") Integer quantity,
            @AuthenticationPrincipal User user) {
        PharmacyDrugInfo drug = pharmacyService.updateStock(id, quantity, user);
        return ResponseEntity.ok(drug);
    }

    @PatchMapping("/price")
    public ResponseEntity<PharmacyDrugInfo> updatePrice(
            @RequestBody PharmacyDrugId id,
            @RequestParam("price") Float price,
            @AuthenticationPrincipal User user) {
        PharmacyDrugInfo drug = pharmacyService.updatePrice(id, price, user);
        return ResponseEntity.ok(drug);
    }

    @DeleteMapping("/")
    public ResponseEntity<Void> deletePharmacyDrug(
            @RequestBody PharmacyDrugId id, @AuthenticationPrincipal User user) {
        pharmacyService.deleteDrug(id);
        return ResponseEntity.noContent().build();
    }
}
