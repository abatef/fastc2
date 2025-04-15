package com.abatef.fastc2.controllers;

import com.abatef.fastc2.dtos.drug.DrugCreationRequest;
import com.abatef.fastc2.dtos.drug.DrugInfo;
import com.abatef.fastc2.models.Drug;
import com.abatef.fastc2.models.User;
import com.abatef.fastc2.services.DrugService;

import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/drugs")
public class DrugController {
    private final DrugService drugService;
    private final ModelMapper modelMapper;

    public DrugController(DrugService drugService, ModelMapper modelMapper) {
        this.drugService = drugService;
        this.modelMapper = modelMapper;
    }

    @PostMapping
    public ResponseEntity<Drug> createDrug(
            @RequestBody DrugCreationRequest request, @AuthenticationPrincipal User user) {
        Drug drug = drugService.createNewDrug(request, user);
        return ResponseEntity.ok(drug);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Drug> getDrugInfo(@PathVariable Integer id) {
        Drug drug = drugService.getDrugByIdOrThrow(id);
        return ResponseEntity.ok(drug);
    }

    @PatchMapping
    public ResponseEntity<DrugInfo> updateDrugInfo(
            @RequestBody DrugInfo info, @AuthenticationPrincipal User user) {
        DrugInfo drug = drugService.updateDrugInfo(info, user);
        return ResponseEntity.ok(drug);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDrug(@PathVariable Integer id) {
        drugService.deleteDrugById(id);
        return ResponseEntity.noContent().build();
    }

//    @PatchMapping("/{id}/full-price")
//    public ResponseEntity<DrugInfo> updateFullPrice(
//            @PathVariable Integer id,
//            @RequestParam("price") Float price,
//            @AuthenticationPrincipal User user) {
//        DrugInfo drugInfo = drugService.updateDrugPrice(id, price, user);
//        return ResponseEntity.ok(drugInfo);
//    }
//
//    @PatchMapping("/{id}/name")
//    public ResponseEntity<DrugInfo> updateName(
//            @PathVariable Integer id,
//            @RequestParam("name") String name,
//            @AuthenticationPrincipal User user) {
//        DrugInfo drugInfo = drugService.updateDrugName(id, name, user);
//        return ResponseEntity.ok(drugInfo);
//    }
//
//    @PatchMapping("/{id}/form")
//    public ResponseEntity<DrugInfo> updateForm(
//            @PathVariable Integer id,
//            @RequestParam("form") String form,
//            @AuthenticationPrincipal User user) {
//        DrugInfo drugInfo = drugService.updateDrugForm(id, form, user);
//        return ResponseEntity.ok(drugInfo);
//    }
//
//    @PatchMapping("/{id}/units")
//    public ResponseEntity<DrugInfo> updateUnits(
//            @PathVariable Integer id,
//            @RequestParam("units") Short units,
//            @AuthenticationPrincipal User user) {
//        DrugInfo drugInfo = drugService.updateDrugUnits(id, units, user);
//        return ResponseEntity.ok(drugInfo);
//    }

    @GetMapping("/search")
    public ResponseEntity<List<DrugInfo>> searchDrugs(
            @RequestParam("name") String name,
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size) {
        List<DrugInfo> drugInfos = drugService.searchByName(name, page, size);
        if (drugInfos.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(drugInfos);
    }
}
