package com.abatef.fastc2.controllers;

import com.abatef.fastc2.dtos.drug.DrugCreationRequest;
import com.abatef.fastc2.dtos.drug.DrugDto;
import com.abatef.fastc2.dtos.drug.DrugUpdateRequest;
import com.abatef.fastc2.enums.UserRole;
import com.abatef.fastc2.models.Drug;
import com.abatef.fastc2.models.User;
import com.abatef.fastc2.services.DrugService;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/drugs")
public class DrugController {
    private final DrugService drugService;
    private final ModelMapper modelMapper;

    public DrugController(DrugService drugService, ModelMapper modelMapper) {
        this.drugService = drugService;
        this.modelMapper = modelMapper;
    }

    @Operation(summary = "Create a new General Drug")
    @PostMapping
    public ResponseEntity<Drug> createDrug(
            @Valid @RequestBody DrugCreationRequest request, @AuthenticationPrincipal User user) {
        Drug drug = drugService.createNewDrug(request, user);
        return ResponseEntity.ok(drug);
    }

    @PostMapping("/fill")
    public ResponseEntity<Drug> fillDB(@RequestBody DrugCreationRequest request) {
        User user = new User();
        user.setId(1);
        user.setRole(UserRole.OWNER);
        Drug drug = drugService.createNewDrug(request, user);
        return ResponseEntity.ok(drug);
    }

    @Operation(summary = "Get General Drug Info By Id")
    @GetMapping("/{id}")
    public ResponseEntity<DrugDto> getDrugInfo(@PathVariable Integer id) {
        DrugDto drug = drugService.getDrugInfoById(id);
        return ResponseEntity.ok(drug);
    }

    @Operation(summary = "Update General Drug Info")
    @PatchMapping
    public ResponseEntity<DrugDto> updateDrugInfo(
            @Valid @RequestBody DrugUpdateRequest info, @AuthenticationPrincipal User user) {
        DrugDto drug = drugService.updateDrugInfo(info, user);
        return ResponseEntity.ok(drug);
    }

    @Operation(summary = "Delete General Drug By Id")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDrug(@PathVariable Integer id) {
        drugService.deleteDrugById(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Search General Drug By Name")
    @GetMapping("/search")
    public ResponseEntity<List<DrugDto>> searchDrugs(
            @RequestParam("name") String name,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "75") int size) {

        PageRequest pageable = PageRequest.of(page, size);
        List<DrugDto> drugDtos =
                drugService.searchByName(name, pageable).stream()
                        .map(drug -> modelMapper.map(drug, DrugDto.class))
                        .collect(Collectors.toList());
        if (drugDtos.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(drugDtos);
    }

    @Operation(summary = "Get All General Drugs Infos")
    @GetMapping("/all")
    public ResponseEntity<List<DrugDto>> getAllDrugs(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "75") int size) {
        PageRequest pageable = PageRequest.of(page, size);
        List<DrugDto> drugDtos = drugService.getAllDrugs(pageable);
        if (drugDtos.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(drugDtos);
    }

    @Operation(summary = "Get Model Id by Drug Id")
    @GetMapping("/model-id")
    public ResponseEntity<Integer> getModelId(@RequestParam("drug_id") Integer id) {
        DrugDto dto = drugService.getDrugInfoById(id);
        return ResponseEntity.ok(dto.getModelId());
    }
}
