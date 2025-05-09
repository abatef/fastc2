package com.abatef.fastc2.controllers;

import com.abatef.fastc2.dtos.drug.*;
import com.abatef.fastc2.dtos.pharmacy.PharmacyCreationRequest;
import com.abatef.fastc2.dtos.pharmacy.PharmacyDto;
import com.abatef.fastc2.dtos.pharmacy.PharmacyUpdateRequest;
import com.abatef.fastc2.dtos.pharmacy.SalesOperationDto;
import com.abatef.fastc2.dtos.user.EmployeeCreationRequest;
import com.abatef.fastc2.dtos.user.EmployeeDto;
import com.abatef.fastc2.enums.*;
import com.abatef.fastc2.models.User;
import com.abatef.fastc2.models.pharmacy.PharmacyDrug;
import com.abatef.fastc2.models.shift.Shift;
import com.abatef.fastc2.services.EmployeeService;
import com.abatef.fastc2.services.PharmacyService;
import com.abatef.fastc2.services.ReportingService;

import io.swagger.v3.oas.annotations.Operation;

import jakarta.validation.Valid;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/pharmacies")
public class PharmacyController {
    private final PharmacyService pharmacyService;
    private final EmployeeService employeeService;
    private final ModelMapper modelMapper;
    private final Logger LOG = LoggerFactory.getLogger(PharmacyController.class);
    private final ReportingService reportingService;

    public PharmacyController(
            PharmacyService pharmacyService,
            EmployeeService employeeService,
            ModelMapper modelMapper,
            ReportingService reportingService) {
        this.pharmacyService = pharmacyService;
        this.employeeService = employeeService;
        this.modelMapper = modelMapper;
        this.reportingService = reportingService;
    }

    @Operation(summary = "Create a new Pharmacy")
    @PostMapping
    public ResponseEntity<PharmacyDto> createPharmacy(
            @Valid @RequestBody PharmacyCreationRequest request,
            @AuthenticationPrincipal User user) {
        PharmacyDto info = pharmacyService.createPharmacy(request, user);
        return ResponseEntity.ok(info);
    }

    @Operation(summary = "Get Pharmacy Info by Id")
    @GetMapping("/{id}")
    public ResponseEntity<PharmacyDto> getPharmacyInfo(
            @PathVariable("id") Integer id, @AuthenticationPrincipal User user) {
        PharmacyDto info = pharmacyService.getPharmacyInfoById(id, user);
        return ResponseEntity.ok(info);
    }

    @Operation(summary = "Delete Pharmacy By Id")
    @DeleteMapping
    public ResponseEntity<Void> deletePharmacy(
            @RequestParam("id") Integer id, @AuthenticationPrincipal User user) {
        pharmacyService.deletePharmacyById(id, user);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get All Pharmacies")
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

    @Operation(summary = "Search for Pharmacy By its Name")
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
            LOG.info("no content, returning empty list");
            return ResponseEntity.noContent().build();
        }
        LOG.info("returning {} drugs", drugs.size());
        return ResponseEntity.ok(drugs);
    }

    @Operation(summary = "Add a Drug to the Pharmacy")
    @PostMapping("/{id}/add")
    public ResponseEntity<PharmacyDrugDto> addDrugToPharmacy(
            @Valid @RequestBody PharmacyDrugCreationRequest request,
            @AuthenticationPrincipal User user,
            @PathVariable("id") Integer pharmacyId) {
        PharmacyDrugDto drug = pharmacyService.addDrugToPharmacy(request, pharmacyId, user);
        return ResponseEntity.ok(drug);
    }

    @Operation(summary = "Get Pharmacy Drug info by Pharmacy Drug Id")
    @GetMapping("/drug")
    public ResponseEntity<PharmacyDrugDto> getPharmacyDrugInfoById(
            @RequestParam("id") Integer id, @AuthenticationPrincipal User user) {
        PharmacyDrugDto info = pharmacyService.getPharmacyDrugInfoById(id, user);
        return ResponseEntity.ok(info);
    }

    @Operation(summary = "Get All Drugs infos in Pharmacy")
    @GetMapping("/{id}/drugs")
    public ResponseEntity<List<PharmacyDrugDto>> getDrugsOfPharmacy(
            @PathVariable("id") Integer id,
            @AuthenticationPrincipal User user,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "75") int size,
            @RequestParam(value = "sort", defaultValue = "expiryDate") String sort) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by(sort).ascending());
        List<PharmacyDrugDto> drugs = pharmacyService.getDrugsByPharmacyId(id, pageable, user);
        return ResponseEntity.ok(drugs);
    }

    @Operation(summary = "Get All Drugs with shortage in pharmacy with shortage info")
    @GetMapping("/{id}/shortage/info")
    public ResponseEntity<List<PharmacyShortageDto>> getShortageDrugs(
            @PathVariable("id") Integer id,
            @RequestParam("drug_id") Integer drugId,
            @AuthenticationPrincipal User user,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size) {
        PageRequest pageable = PageRequest.of(page, size);
        List<PharmacyShortageDto> shortages =
                pharmacyService.getAllShortageDrugsByPharmacyId(id, pageable, user);
        if (shortages.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(shortages);
    }

    @Operation(summary = "Get Order Stats of a Drug")
    @GetMapping("/{id}/drug/order-stats")
    public ResponseEntity<DrugStatsDto> getDrugOrderInfo(
            @PathVariable("id") Integer id,
            @RequestParam("drug_id") Integer drugId,
            @AuthenticationPrincipal User user) {
        DrugStatsDto info = pharmacyService.getDrugOrderInfoByPharmacyAndDrugIds(id, drugId, user);
        if (info == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(info);
    }

    @Operation(summary = "Search for Drugs in the pharmacy with applied filters")
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
            @RequestParam(value = "sort", required = false) SortOption sort,
            @AuthenticationPrincipal User user) {

        LOG.info("Searching drugs with filters for pharmacy: {}", id);

        PageRequest pageable = PageRequest.of(page, size);

        List<PharmacyDrugDto> filteredDrugs =
                pharmacyService.applyAllFiltersJpql(
                        id, null, query, filters, sort, N, price, from, pageable, user);

        return noContentOrReturn(filteredDrugs);
    }

    @Operation(summary = "Get all drugs in pharmacy with filters applied")
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
            @RequestParam(value = "sort", required = false) SortOption sort,
            @AuthenticationPrincipal User user) {
        PageRequest pageable = PageRequest.of(page, size);
        List<PharmacyDrugDto> drugs =
                pharmacyService.applyAllFiltersJpql(
                        id, drugId, null, filters, sort, N, price, from, pageable, user);
        return noContentOrReturn(drugs);
    }

    @Operation(summary = "Get infos of the drugs in the pharmacy by bulk(multiple id at once)")
    @GetMapping("/{id}/drugs/bulk")
    public ResponseEntity<List<PharmacyDrugDto>> bulkInfo(
            @PathVariable("id") Integer phId,
            @RequestParam("ids") List<Integer> drugIds,
            @AuthenticationPrincipal User user) {
        pharmacyService.managerOrEmployeeOrThrow(user, phId);
        List<PharmacyDrugDto> drugs = new ArrayList<>();
        LOG.info("bulk request");
        for (Integer id : drugIds) {
            LOG.info("processing drug id: {}", id);
            PharmacyDrug drug = pharmacyService.getPharmacyDrugByIdOrThrow(id);
            LOG.info("mapping drug of id: {}", id);
            drugs.add(modelMapper.map(drug, PharmacyDrugDto.class));
        }
        return noContentOrReturn(drugs);
    }

    @Operation(summary = "Update Pharmacy Info")
    @PatchMapping
    public ResponseEntity<PharmacyDto> updatePharmacyInfo(
            @Valid @RequestBody PharmacyUpdateRequest pharmacyInfo,
            @AuthenticationPrincipal User user) {
        PharmacyDto info = pharmacyService.updatePharmacyInfo(pharmacyInfo, user);
        return ResponseEntity.ok(info);
    }

    @Operation(summary = "Delete drug from pharmacy by id")
    @DeleteMapping("/{id}/drugs")
    public ResponseEntity<Void> deletePharmacyDrug(
            @RequestParam("drug_id") Integer drugId, @PathVariable("id") Integer pharmacyId) {
        pharmacyService.removeDrugFromPharmacy(drugId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Create a new shift in the Pharmacy")
    @PostMapping("/{id}/shifts")
    public ResponseEntity<PharmacyDto> createPharmacyShift(
            @RequestBody Shift shift,
            @PathVariable("id") Integer pharmacyId,
            @AuthenticationPrincipal User user) {
        PharmacyDto info = pharmacyService.addShiftToPharmacy(pharmacyId, shift, user);
        return ResponseEntity.ok(info);
    }

    @Operation(summary = "Delete Shift from Pharmacy")
    @DeleteMapping("/{id}/shifts")
    public ResponseEntity<Void> deletePharmacyShift(
            @PathVariable("id") Integer id,
            @RequestParam("shift_id") Integer shiftId,
            @AuthenticationPrincipal User user) {
        pharmacyService.removeShiftFromPharmacy(id, shiftId, user);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get all Shifts from the Pharmacy")
    @GetMapping("/{id}/shifts")
    public ResponseEntity<List<Shift>> getShiftsByPharmacy(
            @PathVariable("id") Integer id, @AuthenticationPrincipal User user) {
        List<Shift> shifts = pharmacyService.getShiftsByPharmacyId(id, user);
        if (shifts.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(shifts);
    }

    @Operation(summary = "Create a new Employee and Add it to the Pharmacy")
    @PostMapping("/{id}/employees")
    public ResponseEntity<EmployeeDto> addEmployeeToPharmacy(
            @PathVariable("id") Integer id,
            @Valid @RequestBody EmployeeCreationRequest request,
            @AuthenticationPrincipal User user) {
        request.setPharmacyId(id);
        EmployeeDto employee = employeeService.createNewEmployee(request, user);
        return ResponseEntity.ok(employee);
    }

    @Operation(summary = "Delete an Employee from the Pharmacy")
    @DeleteMapping("/{id}/employees")
    public ResponseEntity<Void> deleteEmployeeFromPharmacy(
            @PathVariable("id") Integer id,
            @RequestParam("employee_id") Integer employeeId,
            @AuthenticationPrincipal User user) {
        pharmacyService.removeEmployeeFromPharmacy(id, employeeId, user);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get All (ACTIVE or INACTIVE) Employees from the Pharmacy")
    @GetMapping("/{id}/employees")
    public ResponseEntity<List<EmployeeDto>> getEmployeesByPharmacy(
            @PathVariable("id") Integer id,
            @AuthenticationPrincipal User user,
            @RequestParam(value = "status", required = false) EmployeeStatus status,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "75") int size) {
        PageRequest pageable = PageRequest.of(page, size);
        List<EmployeeDto> employees =
                pharmacyService.getEmployeesByPharmacyId(id, status, pageable, user);
        if (employees.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(employees);
    }

    @Operation(
            summary =
                    "Get all Sales Reports in the Pharmacy, you can filter by drug_id, type, or status")
    @GetMapping("/{id}/reports/history")
    public ResponseEntity<List<SalesOperationDto>> getAllSalesOperations(
            @PathVariable("id") Integer pharmacyId,
            @RequestParam(required = false) Integer drugId,
            @RequestParam(required = false) Integer receiptId,
            @RequestParam(required = false) Integer orderId,
            @RequestParam(required = false) OperationType type,
            @RequestParam(required = false) OperationStatus status,
            @RequestParam(required = false) Integer cashierId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                    Instant fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                    Instant toDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection) {

        Sort.Direction direction =
                sortDirection.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        List<SalesOperationDto> salesOperations =
                reportingService.applyAllFilters(
                        drugId,
                        pharmacyId,
                        receiptId,
                        orderId,
                        type,
                        status,
                        cashierId,
                        fromDate,
                        toDate,
                        pageable);

        if (salesOperations.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(salesOperations);
    }
}
