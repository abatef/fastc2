package com.abatef.fastc2.controllers;

import com.abatef.fastc2.dtos.drug.PharmacyDrugCreationRequest;
import com.abatef.fastc2.dtos.drug.PharmacyDrugInfo;
import com.abatef.fastc2.dtos.pharmacy.Location;
import com.abatef.fastc2.dtos.pharmacy.PharmacyCreationRequest;
import com.abatef.fastc2.dtos.pharmacy.PharmacyInfo;
import com.abatef.fastc2.models.pharmacy.PharmacyDrugId;
import com.abatef.fastc2.models.User;
import com.abatef.fastc2.services.PharmacyService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/pharmacies")
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
            @RequestParam("drug_id") Integer drugId,
            @RequestParam("pharmacy_id") Integer pharmacyId,
            @RequestParam("expiry_date") LocalDate expiryDate,
            @RequestParam("quantity") Integer quantity,
            @AuthenticationPrincipal User user) {
        PharmacyDrugId id = new PharmacyDrugId(drugId, pharmacyId, expiryDate);
        PharmacyDrugInfo drug = pharmacyService.updateStock(id, quantity, user);
        return ResponseEntity.ok(drug);
    }

    @PatchMapping("/price")
    public ResponseEntity<PharmacyDrugInfo> updatePrice(
            @RequestParam("drug_id") Integer drugId,
            @RequestParam("pharmacy_id") Integer pharmacyId,
            @RequestParam("expiry_date") LocalDate expiryDate,
            @RequestParam("price") Float price,
            @AuthenticationPrincipal User user) {
        PharmacyDrugId id = new PharmacyDrugId(drugId, pharmacyId, expiryDate);
        PharmacyDrugInfo drug = pharmacyService.updatePrice(id, price, user);
        return ResponseEntity.ok(drug);
    }

    @PatchMapping("/threshold")
    public ResponseEntity<PharmacyInfo> updateThreshold(
            @RequestParam("id") Integer id,
            @RequestParam("threshold") Short threshold,
            @AuthenticationPrincipal User user) {
        PharmacyInfo pharmacyInfo = pharmacyService.updateExpiryThreshold(id, threshold, user);
        return ResponseEntity.ok(pharmacyInfo);
    }

    @PatchMapping("/{id}/name")
    public ResponseEntity<PharmacyInfo> updateName(
            @PathVariable Integer id,
            @RequestParam("name") String name,
            @AuthenticationPrincipal User user) {
        PharmacyInfo pharmacyInfo = pharmacyService.updateName(id, name, user);
        return ResponseEntity.ok(pharmacyInfo);
    }

    @PatchMapping("/{id}/address")
    public ResponseEntity<PharmacyInfo> updateAddress(
            @PathVariable Integer id,
            @RequestParam("address") String address,
            @AuthenticationPrincipal User user) {
        PharmacyInfo pharmacyInfo = pharmacyService.updateAddress(id, address, user);
        return ResponseEntity.ok(pharmacyInfo);
    }

    @PatchMapping("/{id}/location")
    public ResponseEntity<PharmacyInfo> updateLocation(
            @PathVariable Integer id,
            @RequestBody Location location,
            @AuthenticationPrincipal User user) {
        PharmacyInfo pharmacyInfo = pharmacyService.updateLocation(id, location, user);
        return ResponseEntity.ok(pharmacyInfo);
    }

    @PatchMapping("{id}/owner")
    public ResponseEntity<PharmacyInfo> updateOwner(
            @PathVariable Integer id,
            @RequestParam("owner") Integer owner,
            @AuthenticationPrincipal User user) {
        PharmacyInfo pharmacyInfo = pharmacyService.updateOwner(id, owner, user);
        return ResponseEntity.ok(pharmacyInfo);
    }

    @DeleteMapping("/")
    public ResponseEntity<Void> deletePharmacyDrug(
            @RequestBody PharmacyDrugId id, @AuthenticationPrincipal User user) {
        pharmacyService.deleteDrug(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<PharmacyInfo>> search(
            @RequestParam("name") String name,
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size) {
        List<PharmacyInfo> list = pharmacyService.searchByName(name, page, size);
        if (list.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(list);
    }
}
