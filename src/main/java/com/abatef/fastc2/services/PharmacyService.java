package com.abatef.fastc2.services;

import com.abatef.fastc2.dtos.drug.PharmacyDrugCreationRequest;
import com.abatef.fastc2.dtos.drug.PharmacyDrugInfo;
import com.abatef.fastc2.dtos.pharmacy.Location;
import com.abatef.fastc2.dtos.pharmacy.PharmacyCreationRequest;
import com.abatef.fastc2.dtos.pharmacy.PharmacyInfo;
import com.abatef.fastc2.enums.ValueType;
import com.abatef.fastc2.exceptions.NonExistingValueException;
import com.abatef.fastc2.exceptions.PharmacyDrugNotFoundException;
import com.abatef.fastc2.models.*;
import com.abatef.fastc2.models.pharmacy.Pharmacy;
import com.abatef.fastc2.models.pharmacy.PharmacyDrug;
import com.abatef.fastc2.models.pharmacy.PharmacyDrugId;
import com.abatef.fastc2.models.shift.PharmacyShift;
import com.abatef.fastc2.models.shift.PharmacyShiftId;
import com.abatef.fastc2.models.shift.Shift;
import com.abatef.fastc2.repositories.EmployeeRepository;
import com.abatef.fastc2.repositories.PharmacyDrugRepository;
import com.abatef.fastc2.repositories.PharmacyRepository;
import com.abatef.fastc2.repositories.PharmacyShiftRepository;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

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

    public PharmacyService(
            PharmacyRepository pharmacyRepository,
            UserService userService,
            DrugService drugService,
            ModelMapper modelMapper,
            ShiftService shiftService,
            PharmacyDrugRepository pharmacyDrugRepository,
            PharmacyShiftRepository pharmacyShiftRepository,
            EmployeeRepository employeeRepository) {
        this.pharmacyRepository = pharmacyRepository;
        this.userService = userService;
        this.drugService = drugService;
        this.modelMapper = modelMapper;
        this.shiftService = shiftService;
        this.pharmacyDrugRepository = pharmacyDrugRepository;
        this.pharmacyShiftRepository = pharmacyShiftRepository;
        this.employeeRepository = employeeRepository;
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

    @Transactional
    public PharmacyInfo updatePharmacyInfo(
            PharmacyInfo pharmacyInfo, @AuthenticationPrincipal User user) {
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

        if (pharmacyInfo.getOwner() != null) {
            pharmacy.setOwner(userService.getUserById(pharmacyInfo.getOwner().getId()));
        }

        if (pharmacyInfo.getExpiryThreshold() != null) {
            pharmacy.setExpiryThreshold(pharmacyInfo.getExpiryThreshold());
        }

        pharmacy = pharmacyRepository.save(pharmacy);
        return modelMapper.map(pharmacy, PharmacyInfo.class);
    }

    @Transactional
    public PharmacyDrugInfo addDrugToPharmacy(PharmacyDrugCreationRequest id, User user) {
        Pharmacy pharmacy = getPharmacyByIdOrThrow(id.getPharmacyId());
        Drug drug = drugService.getDrugByIdOrThrow(id.getDrugId());
        PharmacyDrug pharmacyDrug = new PharmacyDrug(drug, pharmacy, id.getExpiryDate(), user);
        pharmacyDrug.setPrice(id.getPrice());
        pharmacyDrug.setStock(id.getStock());
        pharmacyDrug = pharmacyDrugRepository.save(pharmacyDrug);
        return modelMapper.map(pharmacyDrug, PharmacyDrugInfo.class);
    }

    public List<PharmacyDrugInfo> getDrugsByPharmacy(Integer pharmacyId) {
        List<PharmacyDrug> drugs = pharmacyDrugRepository.getPharmacyDrugsByPharmacy_Id(pharmacyId);
        return drugs.stream()
                .map(drug -> modelMapper.map(drug, PharmacyDrugInfo.class))
                .toList();
    }

    public PharmacyDrug getPharmacyDrugByIdOrThrow(PharmacyDrugId id) {
        Optional<PharmacyDrug> pdOpt = pharmacyDrugRepository.findById(id);
        if (pdOpt.isPresent()) {
            return pdOpt.get();
        }

        PharmacyDrugNotFoundException exception = new PharmacyDrugNotFoundException();
        exception.setId(id);
        if (!drugService.existsById(id.getDrugId())) {
            exception.setWhy(PharmacyDrugNotFoundException.Why.NONEXISTENT_DRUG);
        }
        if (!existsPharmacyById(id.getPharmacyId()) && exception.getWhy() == null) {
            exception.setWhy(PharmacyDrugNotFoundException.Why.NONEXISTENT_PHARMACY);
        }

        if (!pharmacyDrugRepository.existsPharmacyDrugByPharmacy_IdAndDrug_Id(
                id.getPharmacyId(), id.getDrugId())) {
            exception.setWhy(PharmacyDrugNotFoundException.Why.NONEXISTENT_DRUG_PHARMACY);
        }

        if (exception.getWhy() == null) {
            exception.setWhy(PharmacyDrugNotFoundException.Why.NONEXISTENT_WITH_EXPIRY_DATE);
        }
        throw exception;
    }

    @Transactional
    public PharmacyDrugInfo updateStock(PharmacyDrugId id, Integer addedStock, User user) {
        PharmacyDrug drug = getPharmacyDrugByIdOrThrow(id);
        drug.setStock(drug.getStock() + addedStock);
        drug = pharmacyDrugRepository.save(drug);
        return modelMapper.map(drug, PharmacyDrugInfo.class);
    }

    @Transactional
    public PharmacyDrugInfo updatePrice(PharmacyDrugId id, Float newPrice, User user) {
        PharmacyDrug drug = getPharmacyDrugByIdOrThrow(id);
        drug.setPrice(newPrice);
        drug = pharmacyDrugRepository.save(drug);
        return modelMapper.map(drug, PharmacyDrugInfo.class);
    }

    @Transactional
    public void deleteDrug(PharmacyDrugId id) {
        pharmacyDrugRepository.deleteById(id);
    }

    public Boolean pharmacyHasDrug(Integer pharmacyId, Integer drugId) {
        return pharmacyDrugRepository.existsPharmacyDrugByPharmacy_IdAndDrug_Id(pharmacyId, drugId);
    }


    public List<PharmacyInfo> searchByName(String pharmacyName, int page, int size) {
        String formattedName = pharmacyName.trim().toLowerCase().replace(' ', '&');
        return pharmacyRepository
                .searchPharmacyByNamePaginated(
                        pharmacyName, formattedName, PageRequest.of(page, size))
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
        Employee employee = employeeRepository.getEmployeeById(employeeId);
        Pharmacy pharmacy = getPharmacyByIdOrThrow(pharmacyId);
        pharmacy.getEmployees().add(employee);
        pharmacy = pharmacyRepository.save(pharmacy);
        return pharmacy.getEmployees().stream().toList();
    }
}
