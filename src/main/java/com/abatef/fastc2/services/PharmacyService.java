package com.abatef.fastc2.services;

import com.abatef.fastc2.dtos.drug.PharmacyDrugCreationRequest;
import com.abatef.fastc2.dtos.drug.PharmacyDrugInfo;
import com.abatef.fastc2.dtos.pharmacy.Location;
import com.abatef.fastc2.dtos.pharmacy.PharmacyCreationRequest;
import com.abatef.fastc2.dtos.pharmacy.PharmacyInfo;
import com.abatef.fastc2.dtos.pharmacy.PharmacyUpdateRequest;
import com.abatef.fastc2.dtos.user.EmployeeInfo;
import com.abatef.fastc2.enums.FilterOption;
import com.abatef.fastc2.enums.ValueType;
import com.abatef.fastc2.exceptions.NonExistingValueException;
import com.abatef.fastc2.exceptions.PharmacyDrugNotFoundException;
import com.abatef.fastc2.models.*;
import com.abatef.fastc2.models.pharmacy.Pharmacy;
import com.abatef.fastc2.models.pharmacy.PharmacyDrug;
import com.abatef.fastc2.models.shift.PharmacyShift;
import com.abatef.fastc2.models.shift.PharmacyShiftId;
import com.abatef.fastc2.models.shift.Shift;
import com.abatef.fastc2.repositories.EmployeeRepository;
import com.abatef.fastc2.repositories.PharmacyDrugRepository;
import com.abatef.fastc2.repositories.PharmacyRepository;
import com.abatef.fastc2.repositories.PharmacyShiftRepository;

import jakarta.validation.constraints.NotNull;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class PharmacyService {
    private final PharmacyRepository pharmacyRepository;
    private final UserService userService;
    private final DrugService drugService;
    private final ModelMapper modelMapper;
    private final ShiftService shiftService;

    private final PharmacyDrugRepository pharmacyDrugRepository;
    private final PharmacyShiftRepository pharmacyShiftRepository;
    private final EmployeeRepository employeeRepository;
    private final EmployeeService employeeService;

    public PharmacyService(
            PharmacyRepository pharmacyRepository,
            UserService userService,
            DrugService drugService,
            ModelMapper modelMapper,
            ShiftService shiftService,
            PharmacyDrugRepository pharmacyDrugRepository,
            PharmacyShiftRepository pharmacyShiftRepository,
            EmployeeRepository employeeRepository,
            EmployeeService employeeService) {
        this.pharmacyRepository = pharmacyRepository;
        this.userService = userService;
        this.drugService = drugService;
        this.modelMapper = modelMapper;
        this.shiftService = shiftService;
        this.pharmacyDrugRepository = pharmacyDrugRepository;
        this.pharmacyShiftRepository = pharmacyShiftRepository;
        this.employeeRepository = employeeRepository;
        this.employeeService = employeeService;
    }

    @Transactional
    public PharmacyInfo createPharmacy(
            @RequestBody PharmacyCreationRequest request, @AuthenticationPrincipal User user) {
        Pharmacy pharmacy = new Pharmacy();
        pharmacy.setName(request.getName());
        pharmacy.setAddress(request.getAddress());
        pharmacy.setIsBranch(request.getIsBranch());
        pharmacy.setOwner(user);
        if (request.getIsBranch()) {
            Pharmacy mainBranch = getPharmacyByIdOrThrow(request.getMainBranchId());
            pharmacy.setMainBranch(mainBranch);
        }
        pharmacy.setExpiryThreshold(request.getExpiryThreshold());
        if (request.getLocation() != null) {
            pharmacy.setLocation(request.getLocation().toPoint());
        } else {
            pharmacy.setLocation(Location.of(30.1, 32.1).toPoint());
        }
        pharmacy = pharmacyRepository.save(pharmacy);
        return modelMapper.map(pharmacy, PharmacyInfo.class);
    }

    @Transactional
    public PharmacyInfo updateName(Integer id, String name, @AuthenticationPrincipal User user) {
        Pharmacy pharmacy = getPharmacyByIdOrThrow(id);
        pharmacy.setName(name);
        pharmacy = pharmacyRepository.save(pharmacy);
        return modelMapper.map(pharmacy, PharmacyInfo.class);
    }

    @Transactional
    public PharmacyInfo updateAddress(
            Integer id, String address, @AuthenticationPrincipal User user) {
        Pharmacy pharmacy = getPharmacyByIdOrThrow(id);
        pharmacy.setAddress(address);
        pharmacy = pharmacyRepository.save(pharmacy);
        return modelMapper.map(pharmacy, PharmacyInfo.class);
    }

    @Transactional
    public PharmacyInfo updateLocation(
            Integer id, Location location, @AuthenticationPrincipal User user) {
        Pharmacy pharmacy = getPharmacyByIdOrThrow(id);
        pharmacy.setLocation(location.toPoint());
        pharmacy = pharmacyRepository.save(pharmacy);
        return modelMapper.map(pharmacy, PharmacyInfo.class);
    }

    @Transactional
    public PharmacyInfo updateOwner(
            Integer id, Integer ownerId, @AuthenticationPrincipal User user) {
        Pharmacy pharmacy = getPharmacyByIdOrThrow(id);
        User owner = userService.getUserById(ownerId);
        pharmacy.setOwner(owner);
        pharmacy = pharmacyRepository.save(pharmacy);
        return modelMapper.map(pharmacy, PharmacyInfo.class);
    }

    public Pharmacy getPharmacyByIdOrThrow(Integer id) {
        return pharmacyRepository
                .findById(id)
                .orElseThrow(() -> new NonExistingValueException(ValueType.ID, id.toString()));
    }

    public Optional<Pharmacy> getPharmacyById(Integer id) {
        return pharmacyRepository.getPharmacyById(id);
    }

    public Boolean existsPharmacyById(Integer id) {
        return pharmacyRepository.existsPharmacyById(id);
    }

    public PharmacyInfo getPharmacyInfoById(Integer id) {
        return modelMapper.map(getPharmacyByIdOrThrow(id), PharmacyInfo.class);
    }

    @Transactional
    public PharmacyInfo updateExpiryThreshold(Integer id, Short threshold, User user) {
        Pharmacy pharmacy = getPharmacyByIdOrThrow(id);
        pharmacy.setExpiryThreshold(threshold);
        pharmacy = pharmacyRepository.save(pharmacy);
        return modelMapper.map(pharmacy, PharmacyInfo.class);
    }

    @PreAuthorize("hasRole('OWNER')")
    @Transactional
    public PharmacyInfo updatePharmacyInfo(
            @NotNull PharmacyUpdateRequest pharmacyInfo, @AuthenticationPrincipal User user) {
        Pharmacy pharmacy = getPharmacyByIdOrThrow(pharmacyInfo.getId());
        if (pharmacyInfo.getName() != null && !pharmacyInfo.getName().isEmpty()) {
            pharmacy.setName(pharmacyInfo.getName());
        }

        if (pharmacyInfo.getAddress() != null && !pharmacyInfo.getAddress().isEmpty()) {
            pharmacy.setAddress(pharmacyInfo.getAddress());
        }

        if (pharmacyInfo.getLocation() != null) {
            pharmacy.setLocation(pharmacyInfo.getLocation().toPoint());
        }

        if (pharmacyInfo.getExpiryThreshold() != null) {
            pharmacy.setExpiryThreshold(pharmacyInfo.getExpiryThreshold());
        }

        pharmacy = pharmacyRepository.save(pharmacy);
        return modelMapper.map(pharmacy, PharmacyInfo.class);
    }

    @Transactional
    public PharmacyDrugInfo addDrugToPharmacy(PharmacyDrugCreationRequest request, User user) {
        Pharmacy pharmacy = getPharmacyByIdOrThrow(request.getPharmacyId());
        Drug drug = drugService.getDrugByIdOrThrow(request.getDrugId());
        PharmacyDrug pharmacyDrug = new PharmacyDrug();
        pharmacyDrug.setDrug(drug);
        pharmacyDrug.setPharmacy(pharmacy);
        pharmacyDrug.setExpiryDate(request.getExpiryDate());
        pharmacyDrug.setPrice(drug.getFullPrice());
        pharmacyDrug.setStock(request.getStock());
        pharmacyDrug.setAddedBy(user);
        pharmacyDrug = pharmacyDrugRepository.save(pharmacyDrug);
        return modelMapper.map(pharmacyDrug, PharmacyDrugInfo.class);
    }

    public PharmacyDrug getPharmacyDrugByIdOrThrow(Integer id) {
        Optional<PharmacyDrug> pharmacyDrug = pharmacyDrugRepository.findById(id);
        if (pharmacyDrug.isPresent()) {
            return pharmacyDrug.get();
        }
        throw new PharmacyDrugNotFoundException(id);
    }

    public PharmacyDrugInfo getPharmacyDrugInfoById(Integer id) {
        return modelMapper.map(getPharmacyDrugByIdOrThrow(id), PharmacyDrugInfo.class);
    }

    @Transactional
    public void removeDrugFromPharmacy(Integer id, User user) {
        pharmacyDrugRepository.deleteById(id);
    }

    private List<PharmacyDrugInfo> streamAndReturn(Page<PharmacyDrug> drugs) {
        if (drugs.isEmpty()) {
            return List.of();
        }
        return drugs.stream().map(drug -> modelMapper.map(drug, PharmacyDrugInfo.class)).toList();
    }

    public List<PharmacyDrugInfo> getDrugsByPharmacyId(Integer pharmacyId, Pageable pageable) {
        Page<PharmacyDrug> drugs =
                pharmacyDrugRepository.getPharmacyDrugsByPharmacy_Id(pharmacyId, pageable);
        return streamAndReturn(drugs);
    }

    public List<PharmacyDrugInfo> getExpiredDrugsByPharmacyId(
            Integer pharmacyId, Pageable pageable) {
        Page<PharmacyDrug> drugs =
                pharmacyDrugRepository.getPharmacyDrugsByPharmacy_IdAndExpiryDateAfter(
                        pharmacyId, LocalDate.now(), pageable);
        return streamAndReturn(drugs);
    }

    public List<PharmacyDrugInfo> getNonExpiredDrugsByPharmacyId(
            Integer pharmacyId, Pageable pageable) {
        Page<PharmacyDrug> drugs =
                pharmacyDrugRepository.getPharmacyDrugsByPharmacy_IdAndExpiryDateBefore(
                        pharmacyId, LocalDate.now(), pageable);
        return streamAndReturn(drugs);
    }

    public List<PharmacyDrugInfo> getNearExpiredDrugsByPharmacyId(
            Integer pharmacyId, Pageable pageable) {
        Short expiryThreshold = getPharmacyByIdOrThrow(pharmacyId).getExpiryThreshold();
        Page<PharmacyDrug> drugs =
                pharmacyDrugRepository.getPharmacyDrugsByPharmacy_IdAndExpiryDateAfter(
                        pharmacyId, LocalDate.now().plusDays(expiryThreshold), pageable);
        return streamAndReturn(drugs);
    }

    public List<PharmacyDrugInfo> getNearExpiredDrugsAfterNDayByPharmacyId(
            Integer pharmacyId, Integer N, Pageable pageable) {
        Page<PharmacyDrug> pharmacyDrugs =
                pharmacyDrugRepository.getPharmacyDrugsByPharmacy_IdAndExpiryDateAfter(
                        pharmacyId, LocalDate.now().plusDays(N), pageable);
        return streamAndReturn(pharmacyDrugs);
    }

    public List<PharmacyDrugInfo> getOutOfStockDrugsByPharmacyId(
            Integer pharmacyId, Pageable pageable) {
        Page<PharmacyDrug> drugs =
                pharmacyDrugRepository.getPharmacyDrugsByPharmacy_IdAndStockIsLessThanEqual(
                        pharmacyId, 0, pageable);
        return streamAndReturn(drugs);
    }

    public List<PharmacyDrugInfo> getInStockDrugsByPharmacyId(
            Integer pharmacyId, Pageable pageable) {
        Page<PharmacyDrug> drugs =
                pharmacyDrugRepository.getPharmacyDrugsByPharmacy_IdAndStockIsGreaterThanEqual(
                        pharmacyId, 1, pageable);
        return streamAndReturn(drugs);
    }

    public List<PharmacyDrugInfo> getDrugsWithStockOverNByPharmacyId(
            Integer pharmacyId, Integer N, Pageable pageable) {
        Page<PharmacyDrug> drugs =
                pharmacyDrugRepository.getPharmacyDrugsByPharmacy_IdAndStockIsGreaterThanEqual(
                        pharmacyId, N, pageable);
        return streamAndReturn(drugs);
    }

    public List<PharmacyDrugInfo> getDrugsWithStockLessThanNByPharmacyId(
            Integer pharmacyId, Integer N, Pageable pageable) {
        Page<PharmacyDrug> drugs =
                pharmacyDrugRepository.getPharmacyDrugsByPharmacy_IdAndStockIsLessThanEqual(
                        pharmacyId, N, pageable);
        return streamAndReturn(drugs);
    }

    public List<PharmacyDrugInfo> searchDrugInPharmacy(
            String drugName, Integer pharmacyId, Pageable pageable) {
        String formattedName = drugName.trim().toLowerCase().replace(' ', '&');
        Page<PharmacyDrug> pharmacyDrugs =
                pharmacyDrugRepository.searchByDrugName(
                        pharmacyId, drugName, formattedName, pageable);
        if (pharmacyDrugs.isEmpty()) {
            return List.of();
        }
        return streamAndReturn(pharmacyDrugs);
    }

    /*
     * filter -> available, shortage, unavailable shortage, unavailable
     *
     * available -> at least one drug exists in the pharmacy
     * shortage  -> if we have 10, and we require n
     * unavailable shortage -> if we have 0 and require n
     * unavailable -> if we have 0 and require 0
     * */


    public List<PharmacyDrugInfo> filter(Integer pharmacyId, FilterOption filterOption, Pageable pageable) {
        return switch (filterOption) {
            case AVAILABLE -> getDrugsWithStockOverNByPharmacyId(pharmacyId, 1, pageable);
            case SHORTAGE -> getDrugsWithStockLessThanNByPharmacyId(pharmacyId, 10, pageable);
            case UNAVAILABLE_SHORTAGE, UNAVAILABLE -> getOutOfStockDrugsByPharmacyId(pharmacyId, pageable);
        };
    }

    public Boolean pharmacyHasDrug(Integer pharmacyId, Integer drugId) {
        return pharmacyDrugRepository.existsPharmacyDrugByPharmacy_IdAndDrug_Id(pharmacyId, drugId);
    }

    public List<PharmacyInfo> searchByName(String pharmacyName, Pageable pageable) {
        String formattedName = pharmacyName.trim().toLowerCase().replace(' ', '&');
        return pharmacyRepository
                .searchPharmacyByNamePaginated(pharmacyName, formattedName, pageable)
                .stream()
                .map(ph -> modelMapper.map(ph, PharmacyInfo.class))
                .toList();
    }

    @Transactional
    public PharmacyInfo addShiftToPharmacy(Integer pharmacyId, Shift shift, User user) {
        shift = shiftService.create(shift);
        Pharmacy pharmacy = getPharmacyByIdOrThrow(pharmacyId);
        PharmacyShift pharmacyShift = new PharmacyShift();
        pharmacyShift.setPharmacy(pharmacy);
        pharmacyShift.setShift(shift);
        pharmacyShiftRepository.save(pharmacyShift);
        return modelMapper.map(getPharmacyByIdOrThrow(pharmacyId), PharmacyInfo.class);
    }

    @Transactional
    public void removeShiftFromPharmacy(Integer pharmacyId, Integer shiftId, User user) {
        PharmacyShiftId id = new PharmacyShiftId();
        id.setPharmacyId(pharmacyId);
        id.setShiftId(shiftId);
        pharmacyShiftRepository.deleteById(id);
    }

    public List<Shift> getShiftsByPharmacyId(Integer pharmacyId) {
        Pharmacy pharmacy = getPharmacyByIdOrThrow(pharmacyId);
        return pharmacy.getShifts().stream().toList();
    }

    @Transactional
    public List<Employee> addEmployeeToPharmacy(Integer pharmacyId, Integer employeeId, User user) {
        Employee employee = employeeService.getEmployeeByIdOrThrow(employeeId);
        Pharmacy pharmacy = getPharmacyByIdOrThrow(pharmacyId);
        pharmacy.getEmployees().add(employee);
        pharmacy = pharmacyRepository.save(pharmacy);
        return pharmacy.getEmployees().stream().toList();
    }

    public List<EmployeeInfo> getEmployeesByPharmacyId(Integer pharmacyId, Pageable pageable) {
        Page<Employee> employees =
                employeeRepository.getEmployeesByPharmacy_Id(pharmacyId, pageable);
        return employees.stream().map(emp -> modelMapper.map(emp, EmployeeInfo.class)).toList();
    }

    @Transactional
    public void removeEmployeeFromPharmacy(Integer pharmacyId, Integer employeeId, User user) {
        Optional<Employee> employee =
                employeeRepository.getEmployeeByIdAndPharmacy_Id(employeeId, pharmacyId);
        if (employee.isPresent()) {
            Employee employeeToRemove = employee.get();
            employeeToRemove.setPharmacy(null);
            employeeRepository.save(employeeToRemove);
            return;
        }
    }
}
