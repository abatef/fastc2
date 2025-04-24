package com.abatef.fastc2.controllers;

import com.abatef.fastc2.dtos.drug.DrugCreationRequest;
import com.abatef.fastc2.dtos.drug.DrugInfo;
import com.abatef.fastc2.dtos.drug.DrugUpdateRequest;
import com.abatef.fastc2.enums.UserRole;
import com.abatef.fastc2.models.Drug;
import com.abatef.fastc2.models.User;
import com.abatef.fastc2.repositories.UserRepository;
import com.abatef.fastc2.services.DrugService;

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
    private final UserRepository userRepository;

    public DrugController(
            DrugService drugService, ModelMapper modelMapper, UserRepository userRepository) {
        this.drugService = drugService;
        this.modelMapper = modelMapper;
        this.userRepository = userRepository;
    }

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

    @GetMapping("/{id}")
    public ResponseEntity<Drug> getDrugInfo(@PathVariable Integer id) {
        Drug drug = drugService.getDrugByIdOrThrow(id);
        return ResponseEntity.ok(drug);
    }

    @PatchMapping
    public ResponseEntity<DrugInfo> updateDrugInfo(
            @Valid @RequestBody DrugUpdateRequest info, @AuthenticationPrincipal User user) {
        DrugInfo drug = drugService.updateDrugInfo(info, user);
        return ResponseEntity.ok(drug);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDrug(@PathVariable Integer id) {
        drugService.deleteDrugById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<DrugInfo>> searchDrugs(
            @RequestParam("name") String name,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "75") int size) {

        PageRequest pageable = PageRequest.of(page, size);
        List<DrugInfo> drugInfos =
                drugService.searchByName(name, pageable).stream()
                        .map(drug -> modelMapper.map(drug, DrugInfo.class))
                        .collect(Collectors.toList());
        if (drugInfos.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(drugInfos);
    }

    @GetMapping("/all")
    public ResponseEntity<List<DrugInfo>> getAllDrugs(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "75") int size) {
        PageRequest pageable = PageRequest.of(page, size);
        List<DrugInfo> drugInfos = drugService.getAllDrugs(pageable);
        if (drugInfos.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(drugInfos);
    }
}
