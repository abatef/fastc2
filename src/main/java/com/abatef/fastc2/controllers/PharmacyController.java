package com.abatef.fastc2.controllers;

import com.abatef.fastc2.dtos.drug.*;
import com.abatef.fastc2.dtos.pharmacy.PharmacyCreationRequest;
import com.abatef.fastc2.dtos.pharmacy.PharmacyDto;
import com.abatef.fastc2.dtos.pharmacy.PharmacyUpdateRequest;
import com.abatef.fastc2.dtos.user.EmployeeCreationRequest;
import com.abatef.fastc2.dtos.user.EmployeeDto;
import com.abatef.fastc2.enums.EmployeeStatus;
import com.abatef.fastc2.enums.FilterOption;
import com.abatef.fastc2.enums.SortOption;
import com.abatef.fastc2.models.User;
import com.abatef.fastc2.models.pharmacy.PharmacyDrug;
import com.abatef.fastc2.models.shift.Shift;
import com.abatef.fastc2.services.EmployeeService;
import com.abatef.fastc2.services.PharmacyService;

import jakarta.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/pharmacies")
public class PharmacyController {
    private final PharmacyService pharmacyService;
    private final EmployeeService employeeService;
    private final ModelMapper modelMapper;

    public PharmacyController(
            PharmacyService pharmacyService,
            EmployeeService employeeService,
            ModelMapper modelMapper) {
        this.pharmacyService = pharmacyService;
        this.employeeService = employeeService;
        this.modelMapper = modelMapper;
    }

    @PostMapping
    public ResponseEntity<PharmacyDto> createPharmacy(
            @Valid @RequestBody PharmacyCreationRequest request,
            @AuthenticationPrincipal User user) {
        PharmacyDto info = pharmacyService.createPharmacy(request, user);
        return ResponseEntity.ok(info);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PharmacyDto> getPharmacyInfo(@PathVariable("id") Integer id) {
        PharmacyDto info = pharmacyService.getPharmacyInfoById(id);
        return ResponseEntity.ok(info);
    }

    @DeleteMapping
    public ResponseEntity<Void> deletePharmacy(
            @RequestParam("id") Integer id, @AuthenticationPrincipal User user) {
        pharmacyService.deletePharmacyById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/all")
    public ResponseEntity<List<PharmacyDto>> getAllPharmacies(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "75") int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        List<PharmacyDto> infos = pharmacyService.getAllPharmacies(pageRequest);
        if (infos.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(infos);
    }

    @GetMapping("/search")
    public ResponseEntity<List<PharmacyDto>> search(
            @RequestParam("name") String name,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "75") int size) {
        PageRequest pageable = PageRequest.of(page, size);
        List<PharmacyDto> list = pharmacyService.searchByName(name, pageable);
        if (list.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(list);
    }

    private ResponseEntity<List<PharmacyDrugDto>> noContentOrReturn(List<PharmacyDrugDto> drugs) {
        if (drugs.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(drugs);
    }

    @PostMapping("/{id}/add")
    public ResponseEntity<PharmacyDrugDto> addDrugToPharmacy(
            @Valid @RequestBody PharmacyDrugCreationRequest request,
            @AuthenticationPrincipal User user,
            @PathVariable("id") Integer pharmacyId) {
        PharmacyDrugDto drug = pharmacyService.addDrugToPharmacy(request, pharmacyId, user);
        return ResponseEntity.ok(drug);
    }

    @GetMapping("/drug")
    public ResponseEntity<PharmacyDrugDto> getPharmacyDrugInfoById(
            @RequestParam("id") Integer id, @AuthenticationPrincipal User user) {
        PharmacyDrugDto info = pharmacyService.getPharmacyDrugInfoById(id);
        return ResponseEntity.ok(info);
    }

    @GetMapping("/{id}/drugs")
    public ResponseEntity<List<PharmacyDrugDto>> getDrugsOfPharmacy(
            @PathVariable("id") Integer id,
            @AuthenticationPrincipal User user,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "75") int size,
            @RequestParam(value = "sort", defaultValue = "expiryDate") String sort) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by(sort).ascending());
        List<PharmacyDrugDto> drugs = pharmacyService.getDrugsByPharmacyId(id, pageable);
        return noContentOrReturn(drugs);
    }

    @GetMapping("/{id}/shortage/info")
    public ResponseEntity<List<PharmacyShortageDto>> getShortageDrugs(
            @PathVariable("id") Integer id,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size) {
        PageRequest pageable = PageRequest.of(page, size);
        List<PharmacyShortageDto> shortages =
                pharmacyService.getAllShortageDrugsByPharmacyId(id, pageable);
        if (shortages.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(shortages);
    }

    @GetMapping("/{id}/drug/order-stats")
    public ResponseEntity<DrugStatsDto> getDrugOrderInfo(
            @PathVariable("id") Integer id, @RequestParam("drug_id") Integer drugId) {
        DrugStatsDto info = pharmacyService.getDrugOrderInfoByPharmacyAndDrugIds(id, drugId);
        if (info == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(info);
    }

    @GetMapping("/{id}/drugs/search")
    public ResponseEntity<List<PharmacyDrugDto>> searchDrugs(
            @PathVariable("id") Integer id,
            @RequestParam(value = "search", required = false) String query,
            @RequestParam(value = "filter", required = false) List<FilterOption> filters,
            @RequestParam(value = "N", required = false) Integer N,
            @RequestParam(value = "price", required = false) Float price,
            @RequestParam(value = "from", required = false) String from,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "75") int size,
            @RequestParam(value = "sort", required = false) SortOption sort) {
        PageRequest pageable = PageRequest.of(page, size);
        List<PharmacyDrugDto> drugs =
                pharmacyService.applyAllFilters(
                        id, null, query, filters, sort, N, price, from, pageable);
        return noContentOrReturn(drugs);
    }

    @GetMapping("/{id}/drugs/filter")
    public ResponseEntity<List<PharmacyDrugDto>> filterDrugs(
            @PathVariable("id") Integer id,
            @RequestParam(value = "filter", required = false) List<FilterOption> filters,
            @RequestParam(value = "N", required = false) Integer N,
            @RequestParam(value = "drug_id", required = false) Integer drugId,
            @RequestParam(value = "price", required = false) Float price,
            @RequestParam(value = "from", required = false) String from,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "75") int size,
            @RequestParam(value = "sort", required = false) SortOption sort) {
        PageRequest pageable = PageRequest.of(page, size);
        List<PharmacyDrugDto> drugs =
                pharmacyService.applyAllFilters(
                        id, drugId, null, filters, sort, N, price, from, pageable);
        return noContentOrReturn(drugs);
    }

    @GetMapping("/{id}/drugs/bulk")
    public ResponseEntity<List<PharmacyDrugDto>> bulkInfo(
            @PathVariable("id") Integer phId, @RequestBody List<Integer> drugIds) {
        List<PharmacyDrugDto> drugs = new ArrayList<>();
        for (Integer id : drugIds) {
            PharmacyDrug drug = pharmacyService.getPharmacyDrugByIdOrThrow(id);
            drugs.add(modelMapper.map(drug, PharmacyDrugDto.class));
        }
        return noContentOrReturn(drugs);
    }

    @PatchMapping
    public ResponseEntity<PharmacyDto> updatePharmacyInfo(
            @Valid @RequestBody PharmacyUpdateRequest pharmacyInfo,
            @AuthenticationPrincipal User user) {
        PharmacyDto info = pharmacyService.updatePharmacyInfo(pharmacyInfo, user);
        return ResponseEntity.ok(info);
    }

    @DeleteMapping("/{id}/drugs")
    public ResponseEntity<Void> deletePharmacyDrug(
            @RequestParam("drug_id") Integer drugId, @PathVariable("id") Integer pharmacyId) {
        pharmacyService.removeDrugFromPharmacy(drugId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/shifts")
    public ResponseEntity<PharmacyDto> createPharmacyShift(
            @RequestBody Shift shift,
            @PathVariable("id") Integer pharmacyId,
            @AuthenticationPrincipal User user) {
        PharmacyDto info = pharmacyService.addShiftToPharmacy(pharmacyId, shift, user);
        return ResponseEntity.ok(info);
    }

    @DeleteMapping("/{id}/shifts")
    public ResponseEntity<Void> deletePharmacyShift(
            @PathVariable("id") Integer id,
            @RequestParam("shift_id") Integer shiftId,
            @AuthenticationPrincipal User user) {
        pharmacyService.removeShiftFromPharmacy(id, shiftId, user);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/shifts")
    public ResponseEntity<List<Shift>> getShiftsByPharmacy(@PathVariable("id") Integer id) {
        List<Shift> shifts = pharmacyService.getShiftsByPharmacyId(id);
        if (shifts.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(shifts);
    }

    @PostMapping("/{id}/employees")
    public ResponseEntity<EmployeeDto> addEmployeeToPharmacy(
            @PathVariable("id") Integer id,
            @Valid @RequestBody EmployeeCreationRequest request,
            @AuthenticationPrincipal User user) {
        request.setPharmacyId(id);
        EmployeeDto employee = employeeService.createNewEmployee(request, user);
        return ResponseEntity.ok(employee);
    }

    @DeleteMapping("/{id}/employees")
    public ResponseEntity<Void> deleteEmployeeFromPharmacy(
            @PathVariable("id") Integer id,
            @RequestParam("employee_id") Integer employeeId,
            @AuthenticationPrincipal User user) {
        pharmacyService.removeEmployeeFromPharmacy(id, employeeId, user);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/employees")
    public ResponseEntity<List<EmployeeDto>> getEmployeesByPharmacy(
            @PathVariable("id") Integer id,
            @RequestParam(value = "status", required = false) EmployeeStatus status,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "75") int size) {
        PageRequest pageable = PageRequest.of(page, size);
        List<EmployeeDto> employees =
                pharmacyService.getEmployeesByPharmacyId(id, status, pageable);
        if (employees.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(employees);
    }
}
