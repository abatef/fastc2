package com.abatef.fastc2.controllers;

import com.abatef.fastc2.dtos.drug.DrugCreationRequest;
import com.abatef.fastc2.dtos.drug.DrugInfo;
import com.abatef.fastc2.models.Drug;
import com.abatef.fastc2.models.User;
import com.abatef.fastc2.services.DrugService;
import org.geolatte.geom.V;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/drug")
public class DrugController {
    private final DrugService drugService;

    public DrugController(DrugService drugService) {
        this.drugService = drugService;
    }

    @PostMapping("/")
    public ResponseEntity<Drug> createDrug(@RequestBody DrugCreationRequest request, @AuthenticationPrincipal User user) {
        Drug drug = drugService.createNewDrug(request, user);
        return ResponseEntity.ok(drug);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Drug> getDrugInfo(@PathVariable Integer id) {
        Drug drug = drugService.getDrugById(id);
        return ResponseEntity.ok(drug);
    }

    @PatchMapping("/")
    public ResponseEntity<Drug> updateDrugInfo(@RequestBody DrugInfo info, @AuthenticationPrincipal User user) {
        Drug drug = drugService.updateDrugInfo(info, user);
        return ResponseEntity.ok(drug);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDrug(@PathVariable Integer id) {
        drugService.deleteDrugById(id);
        return ResponseEntity.noContent().build();
    }

}
