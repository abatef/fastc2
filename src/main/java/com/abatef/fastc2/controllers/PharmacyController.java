package com.abatef.fastc2.controllers;

import com.abatef.fastc2.dtos.drug.PharmacyDrugCreationRequest;
import com.abatef.fastc2.dtos.drug.PharmacyDrugInfo;
import com.abatef.fastc2.dtos.pharmacy.PharmacyCreationRequest;
import com.abatef.fastc2.dtos.pharmacy.PharmacyInfo;
import com.abatef.fastc2.dtos.pharmacy.PharmacyUpdateRequest;
import com.abatef.fastc2.dtos.user.EmployeeCreationRequest;
import com.abatef.fastc2.dtos.user.EmployeeInfo;
import com.abatef.fastc2.models.User;
import com.abatef.fastc2.models.shift.Shift;
import com.abatef.fastc2.services.EmployeeService;
import com.abatef.fastc2.services.PharmacyService;

import jakarta.validation.Valid;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/pharmacies")
public class PharmacyController {
    private final PharmacyService pharmacyService;
    private final EmployeeService employeeService;

    public PharmacyController(PharmacyService pharmacyService, EmployeeService employeeService) {
        this.pharmacyService = pharmacyService;
        this.employeeService = employeeService;
    }

    @PostMapping
    public ResponseEntity<PharmacyInfo> createPharmacy(
            @Valid @RequestBody PharmacyCreationRequest request,
            @AuthenticationPrincipal User user) {
        PharmacyInfo info = pharmacyService.createPharmacy(request, user);
        return ResponseEntity.ok(info);
    }

    @GetMapping("/{pharmacy_id}")
    public ResponseEntity<PharmacyInfo> getPharmacyInfo(@PathVariable("pharmacy_id") Integer id) {
        PharmacyInfo info = pharmacyService.getPharmacyInfoById(id);
        return ResponseEntity.ok(info);
    }

    @GetMapping("/search")
    public ResponseEntity<List<PharmacyInfo>> search(
            @RequestParam("name") String name,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sort", required = false, defaultValue = "id") String sort) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by(sort));
        List<PharmacyInfo> list = pharmacyService.searchByName(name, pageable);
        if (list.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(list);
    }

    private ResponseEntity<List<PharmacyDrugInfo>> noContentOrReturn(List<PharmacyDrugInfo> drugs) {
        if (drugs.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(drugs);
    }

    @PostMapping("/add")
    public ResponseEntity<PharmacyDrugInfo> addDrugToPharmacy(
            @Valid @RequestBody PharmacyDrugCreationRequest request,
            @AuthenticationPrincipal User user) {
        PharmacyDrugInfo drug = pharmacyService.addDrugToPharmacy(request, user);
        return ResponseEntity.ok(drug);
    }

    @GetMapping("/drug")
    public ResponseEntity<PharmacyDrugInfo> getPharmacyDrugInfoById(
            @RequestParam("id") Integer id, @AuthenticationPrincipal User user) {
        PharmacyDrugInfo info = pharmacyService.getPharmacyDrugInfoById(id);
        return ResponseEntity.ok(info);
    }

    @GetMapping("/{pharmacy_id}/drugs")
    public ResponseEntity<List<PharmacyDrugInfo>> getDrugsOfPharmacy(
            @PathVariable("pharmacy_id") Integer id,
            @AuthenticationPrincipal User user,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sort", required = false, defaultValue = "id") String sort) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by(sort));
        List<PharmacyDrugInfo> drugs = pharmacyService.getDrugsByPharmacyId(id, pageable);
        return noContentOrReturn(drugs);
    }

    @GetMapping("/{pharmacy_id}/expired")
    public ResponseEntity<List<PharmacyDrugInfo>> getExpiredDrugs(
            @PathVariable("pharmacy_id") Integer id,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sort", required = false, defaultValue = "id") String sort) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by(sort));
        List<PharmacyDrugInfo> drugs = pharmacyService.getExpiredDrugsByPharmacyId(id, pageable);
        return noContentOrReturn(drugs);
    }

    @GetMapping("/{pharmacy_id}/non-expired")
    public ResponseEntity<List<PharmacyDrugInfo>> getNonExpiredDrugs(
            @PathVariable("pharmacy_id") Integer id,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sort", required = false, defaultValue = "id") String sort) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by(sort));
        List<PharmacyDrugInfo> drugs = pharmacyService.getNonExpiredDrugsByPharmacyId(id, pageable);
        return noContentOrReturn(drugs);
    }

    @GetMapping("/{pharmacy_id}/past-threshold")
    public ResponseEntity<List<PharmacyDrugInfo>> getNearExpiryDrugs(
            @PathVariable("pharmacy_id") Integer id,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sort", required = false, defaultValue = "id") String sort) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by(sort));
        List<PharmacyDrugInfo> drugs =
                pharmacyService.getNearExpiredDrugsByPharmacyId(id, pageable);
        return noContentOrReturn(drugs);
    }

    @GetMapping("/{pharmacy_id}/after-days")
    public ResponseEntity<List<PharmacyDrugInfo>> getAfterDaysDrugs(
            @PathVariable("pharmacy_id") Integer id,
            @RequestParam(value = "days", defaultValue = "0") Integer N,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sort", required = false, defaultValue = "id") String sort) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by(sort));
        List<PharmacyDrugInfo> drugs =
                pharmacyService.getNearExpiredDrugsAfterNDayByPharmacyId(id, N, pageable);
        return noContentOrReturn(drugs);
    }

    @GetMapping("/{pharmacy_id}/out-of-stock")
    public ResponseEntity<List<PharmacyDrugInfo>> getOutOfStockDrugs(
            @PathVariable("pharmacy_id") Integer id,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sort", required = false, defaultValue = "id") String sort) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by(sort));
        List<PharmacyDrugInfo> drugs = pharmacyService.getOutOfStockDrugsByPharmacyId(id, pageable);
        return noContentOrReturn(drugs);
    }

    @GetMapping("/{pharmacy_id}/in-stock")
    public ResponseEntity<List<PharmacyDrugInfo>> getInStockDrugs(
            @PathVariable("pharmacy_id") Integer id,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sort", required = false, defaultValue = "id") String sort) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by(sort));
        List<PharmacyDrugInfo> drugs = pharmacyService.getInStockDrugsByPharmacyId(id, pageable);
        return noContentOrReturn(drugs);
    }

    @GetMapping("/{pharmacy_id}/stock-over-n")
    public ResponseEntity<List<PharmacyDrugInfo>> getStockOverNDrugs(
            @PathVariable("pharmacy_id") Integer id,
            @RequestParam(value = "n", defaultValue = "0") Integer N,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sort", required = false, defaultValue = "id") String sort) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by(sort));
        List<PharmacyDrugInfo> drugs =
                pharmacyService.getDrugsWithStockOverNByPharmacyId(id, N, pageable);
        return noContentOrReturn(drugs);
    }

    @GetMapping("/{pharmacy_id}/stock-under-n")
    public ResponseEntity<List<PharmacyDrugInfo>> getStockUnderNDrugs(
            @PathVariable("pharmacy_id") Integer id,
            Integer N,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sort", required = false, defaultValue = "id") String sort) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by(sort));
        List<PharmacyDrugInfo> drugs =
                pharmacyService.getDrugsWithStockLessThanNByPharmacyId(id, N, pageable);
        return noContentOrReturn(drugs);
    }

    @GetMapping("/{pharmacy_id}/drugs/search")
    public ResponseEntity<List<PharmacyDrugInfo>> searchDrugs(
            @PathVariable("pharmacy_id") Integer id,
            @RequestParam("q") String query,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sort", required = false, defaultValue = "id") String sort) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by(sort));
        List<PharmacyDrugInfo> drugs = pharmacyService.searchDrugInPharmacy(query, id, pageable);
        return noContentOrReturn(drugs);
    }

    @PatchMapping
    public ResponseEntity<PharmacyInfo> updatePharmacyInfo(
            @Valid @RequestBody PharmacyUpdateRequest pharmacyInfo,
            @AuthenticationPrincipal User user) {
        PharmacyInfo info = pharmacyService.updatePharmacyInfo(pharmacyInfo, user);
        return ResponseEntity.ok(info);
    }

    @DeleteMapping
    public ResponseEntity<Void> deletePharmacyDrug(
            @RequestParam("id") Integer id, @AuthenticationPrincipal User user) {
        pharmacyService.removeDrugFromPharmacy(id, user);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{pharmacy_id}/shifts")
    public ResponseEntity<PharmacyInfo> createPharmacyShift(
            @RequestBody Shift shift,
            @PathVariable("pharmacy_id") Integer pharmacyId,
            @AuthenticationPrincipal User user) {
        PharmacyInfo info = pharmacyService.addShiftToPharmacy(pharmacyId, shift, user);
        return ResponseEntity.ok(info);
    }

    @DeleteMapping("/{pharmacy_id}/shifts")
    public ResponseEntity<Void> deletePharmacyShift(
            @PathVariable("pharmacy_id") Integer id,
            @RequestParam("shift_id") Integer shiftId,
            @AuthenticationPrincipal User user) {
        pharmacyService.removeShiftFromPharmacy(id, shiftId, user);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{pharmacy_id}/shifts")
    public ResponseEntity<List<Shift>> getShiftsByPharmacy(
            @PathVariable("pharmacy_id") Integer id) {
        List<Shift> shifts = pharmacyService.getShiftsByPharmacyId(id);
        if (shifts.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(shifts);
    }

    @PostMapping("/{pharmacy_id}/employees")
    public ResponseEntity<EmployeeInfo> addEmployeeToPharmacy(
            @PathVariable("pharmacy_id") Integer id,
            @Valid @RequestBody EmployeeCreationRequest request,
            @AuthenticationPrincipal User user) {
        request.setPharmacyId(id);
        EmployeeInfo employee = employeeService.createNewEmployee(request, user);
        return ResponseEntity.ok(employee);
    }

    @DeleteMapping("/{pharmacy_id}/employees")
    public ResponseEntity<Void> deleteEmployeeFromPharmacy(
            @PathVariable("pharmacy_id") Integer id,
            @RequestParam("employee_id") Integer employeeId,
            @AuthenticationPrincipal User user) {
        pharmacyService.removeEmployeeFromPharmacy(id, employeeId, user);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{pharmacy_id}/employees")
    public ResponseEntity<List<EmployeeInfo>> getEmployeesByPharmacy(
            @PathVariable("pharmacy_id") Integer id,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sort", required = false, defaultValue = "id") String sort) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by(sort));
        List<EmployeeInfo> employees = pharmacyService.getEmployeesByPharmacyId(id, pageable);
        if (employees.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(employees);
    }
}
