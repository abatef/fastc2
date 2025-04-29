package com.abatef.fastc2.services;

import com.abatef.fastc2.dtos.drug.*;
import com.abatef.fastc2.dtos.pharmacy.Location;
import com.abatef.fastc2.dtos.pharmacy.PharmacyCreationRequest;
import com.abatef.fastc2.dtos.pharmacy.PharmacyDto;
import com.abatef.fastc2.dtos.pharmacy.PharmacyUpdateRequest;
import com.abatef.fastc2.dtos.user.EmployeeDto;
import com.abatef.fastc2.enums.FilterOption;
import com.abatef.fastc2.enums.ValueType;
import com.abatef.fastc2.exceptions.NonExistingValueException;
import com.abatef.fastc2.exceptions.PharmacyDrugNotFoundException;
import com.abatef.fastc2.models.*;
import com.abatef.fastc2.models.pharmacy.DrugOrder;
import com.abatef.fastc2.models.pharmacy.DrugOrderId;
import com.abatef.fastc2.models.pharmacy.Pharmacy;
import com.abatef.fastc2.models.pharmacy.PharmacyDrug;
import com.abatef.fastc2.models.shift.PharmacyShift;
import com.abatef.fastc2.models.shift.PharmacyShiftId;
import com.abatef.fastc2.models.shift.Shift;
import com.abatef.fastc2.repositories.*;

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
import java.util.*;

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
    private final DrugOrderRepository drugOrderRepository;

    public PharmacyService(
            PharmacyRepository pharmacyRepository,
            UserService userService,
            DrugService drugService,
            ModelMapper modelMapper,
            ShiftService shiftService,
            PharmacyDrugRepository pharmacyDrugRepository,
            PharmacyShiftRepository pharmacyShiftRepository,
            EmployeeRepository employeeRepository,
            EmployeeService employeeService,
            DrugOrderRepository drugOrderRepository) {
        this.pharmacyRepository = pharmacyRepository;
        this.userService = userService;
        this.drugService = drugService;
        this.modelMapper = modelMapper;
        this.shiftService = shiftService;
        this.pharmacyDrugRepository = pharmacyDrugRepository;
        this.pharmacyShiftRepository = pharmacyShiftRepository;
        this.employeeRepository = employeeRepository;
        this.employeeService = employeeService;
        this.drugOrderRepository = drugOrderRepository;
    }

    @Transactional
    public PharmacyDto createPharmacy(
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
        return modelMapper.map(pharmacy, PharmacyDto.class);
    }

    @Transactional
    public PharmacyDto updateName(Integer id, String name, @AuthenticationPrincipal User user) {
        Pharmacy pharmacy = getPharmacyByIdOrThrow(id);
        pharmacy.setName(name);
        pharmacy = pharmacyRepository.save(pharmacy);
        return modelMapper.map(pharmacy, PharmacyDto.class);
    }

    @Transactional
    public PharmacyDto updateAddress(
            Integer id, String address, @AuthenticationPrincipal User user) {
        Pharmacy pharmacy = getPharmacyByIdOrThrow(id);
        pharmacy.setAddress(address);
        pharmacy = pharmacyRepository.save(pharmacy);
        return modelMapper.map(pharmacy, PharmacyDto.class);
    }

    @Transactional
    public PharmacyDto updateLocation(
            Integer id, Location location, @AuthenticationPrincipal User user) {
        Pharmacy pharmacy = getPharmacyByIdOrThrow(id);
        pharmacy.setLocation(location.toPoint());
        pharmacy = pharmacyRepository.save(pharmacy);
        return modelMapper.map(pharmacy, PharmacyDto.class);
    }

    @Transactional
    public PharmacyDto updateOwner(
            Integer id, Integer ownerId, @AuthenticationPrincipal User user) {
        Pharmacy pharmacy = getPharmacyByIdOrThrow(id);
        User owner = userService.getUserById(ownerId);
        pharmacy.setOwner(owner);
        pharmacy = pharmacyRepository.save(pharmacy);
        return modelMapper.map(pharmacy, PharmacyDto.class);
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

    public PharmacyDto getPharmacyInfoById(Integer id) {
        return modelMapper.map(getPharmacyByIdOrThrow(id), PharmacyDto.class);
    }

    @Transactional
    public PharmacyDto updateExpiryThreshold(Integer id, Short threshold, User user) {
        Pharmacy pharmacy = getPharmacyByIdOrThrow(id);
        pharmacy.setExpiryThreshold(threshold);
        pharmacy = pharmacyRepository.save(pharmacy);
        return modelMapper.map(pharmacy, PharmacyDto.class);
    }

    public List<PharmacyDto> getAllPharmacies(Pageable pageable) {
        return pharmacyRepository.findAll(pageable).stream()
                .map(ph -> modelMapper.map(ph, PharmacyDto.class))
                .toList();
    }

    @PreAuthorize("hasRole('OWNER')")
    @Transactional
    public PharmacyDto updatePharmacyInfo(
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
        return modelMapper.map(pharmacy, PharmacyDto.class);
    }

    @Transactional
    public PharmacyDrugDto addDrugToPharmacy(
            PharmacyDrugCreationRequest request, Integer pharmacyId, User user) {
        Pharmacy pharmacy = getPharmacyByIdOrThrow(pharmacyId);
        Drug drug = drugService.getDrugByIdOrThrow(request.getDrugId());
        PharmacyDrug pharmacyDrug = new PharmacyDrug();
        pharmacyDrug.setDrug(drug);
        pharmacyDrug.setPharmacy(pharmacy);
        pharmacyDrug.setExpiryDate(request.getExpiryDate());
        pharmacyDrug.setPrice(drug.getFullPrice());
        pharmacyDrug.setStock(request.getStock() * drug.getUnits());
        pharmacyDrug.setAddedBy(user);
        pharmacyDrug = pharmacyDrugRepository.save(pharmacyDrug);
        DrugOrderId id = new DrugOrderId(drug.getId(), pharmacy.getId());
        Optional<DrugOrder> drugOrderOptional = drugOrderRepository.getDrugOrderById(id);
        DrugOrder order;
        if (drugOrderOptional.isPresent()) {
            order = drugOrderOptional.get();
            Integer oldRequired = order.getRequired();
            Integer oldNOrders = order.getNOrders();
            order.setNOrders(oldNOrders + 1);
            order.setRequired(oldRequired + request.getStock());
        } else {
            order = new DrugOrder();
            order.setId(id);
            order.setDrug(drug);
            order.setPharmacy(pharmacy);
            order.setRequired(request.getStock());
            order.setNOrders(1);
        }
        drugOrderRepository.save(order);
        return modelMapper.map(pharmacyDrug, PharmacyDrugDto.class);
    }

    public PharmacyDrug getPharmacyDrugByIdOrThrow(Integer id) {
        Optional<PharmacyDrug> pharmacyDrug = pharmacyDrugRepository.findById(id);
        if (pharmacyDrug.isPresent()) {
            return pharmacyDrug.get();
        }
        throw new PharmacyDrugNotFoundException(id);
    }

    public PharmacyDrugDto getPharmacyDrugInfoById(Integer id) {
        return modelMapper.map(getPharmacyDrugByIdOrThrow(id), PharmacyDrugDto.class);
    }

    @Transactional
    public void removeDrugFromPharmacy(Integer id, User user) {
        pharmacyDrugRepository.deleteById(id);
    }

    private List<PharmacyDrugDto> streamAndReturn(Page<PharmacyDrug> drugs) {
        if (drugs.isEmpty()) {
            return List.of();
        }
        return drugs.stream().map(drug -> modelMapper.map(drug, PharmacyDrugDto.class)).toList();
    }

    public PharmacyDrug getNextDrugToSell(Integer pharmacyId, Integer drugId) {
        List<PharmacyDrug> drugs =
                pharmacyDrugRepository.getAllByPharmacy_IdAndDrug_IdAndStockGreaterThan(
                        pharmacyId, drugId, 0);
        return drugs.stream()
                .min(Comparator.comparing(PharmacyDrug::getExpiryDate))
                .orElse(null);
    }

    public List<PharmacyDrugDto> getDrugsByPharmacyId(Integer pharmacyId, Pageable pageable) {
        Page<PharmacyDrug> drugs =
                pharmacyDrugRepository.getPharmacyDrugsByPharmacy_Id(pharmacyId, pageable);
        return streamAndReturn(drugs);
    }

    public List<PharmacyDrugDto> getExpiredDrugsByPharmacyId(
            Integer pharmacyId, Pageable pageable) {
        Page<PharmacyDrug> drugs =
                pharmacyDrugRepository.getPharmacyDrugsByPharmacy_IdAndExpiryDateAfter(
                        pharmacyId, LocalDate.now(), pageable);
        return streamAndReturn(drugs);
    }

    public List<PharmacyDrugDto> getNonExpiredDrugsByPharmacyId(
            Integer pharmacyId, Pageable pageable) {
        Page<PharmacyDrug> drugs =
                pharmacyDrugRepository.getPharmacyDrugsByPharmacy_IdAndExpiryDateBefore(
                        pharmacyId, LocalDate.now(), pageable);
        return streamAndReturn(drugs);
    }

    public List<PharmacyDrugDto> getNearExpiredDrugsByPharmacyId(
            Integer pharmacyId, Pageable pageable) {
        Short expiryThreshold = getPharmacyByIdOrThrow(pharmacyId).getExpiryThreshold();
        Page<PharmacyDrug> drugs =
                pharmacyDrugRepository.getPharmacyDrugsByPharmacy_IdAndExpiryDateAfter(
                        pharmacyId, LocalDate.now().plusDays(expiryThreshold), pageable);
        return streamAndReturn(drugs);
    }

    public List<PharmacyDrugDto> getNearExpiredDrugsAfterNDayByPharmacyId(
            Integer pharmacyId, Integer N, Pageable pageable) {
        Page<PharmacyDrug> pharmacyDrugs =
                pharmacyDrugRepository.getPharmacyDrugsByPharmacy_IdAndExpiryDateAfter(
                        pharmacyId, LocalDate.now().plusDays(N), pageable);
        return streamAndReturn(pharmacyDrugs);
    }

    public List<PharmacyDrugDto> getOutOfStockDrugsByPharmacyId(
            Integer pharmacyId, Pageable pageable) {
        Page<PharmacyDrug> drugs =
                pharmacyDrugRepository.getPharmacyDrugsByPharmacy_IdAndStockIsLessThanEqual(
                        pharmacyId, 0, pageable);
        return streamAndReturn(drugs);
    }

    public List<PharmacyDrugDto> getInStockDrugsByPharmacyId(
            Integer pharmacyId, Pageable pageable) {
        Page<PharmacyDrug> drugs =
                pharmacyDrugRepository.getPharmacyDrugsByPharmacy_IdAndStockIsGreaterThanEqual(
                        pharmacyId, 1, pageable);
        return streamAndReturn(drugs);
    }

    public List<PharmacyDrugDto> getDrugsWithStockOverNByPharmacyId(
            Integer pharmacyId, Integer N, Pageable pageable) {
        Page<PharmacyDrug> drugs =
                pharmacyDrugRepository.getPharmacyDrugsByPharmacy_IdAndStockIsGreaterThanEqual(
                        pharmacyId, N, pageable);
        return streamAndReturn(drugs);
    }

    public List<PharmacyDrugDto> getDrugsWithStockLessThanNByPharmacyId(
            Integer pharmacyId, Integer N, Pageable pageable) {
        Page<PharmacyDrug> drugs =
                pharmacyDrugRepository.getPharmacyDrugsByPharmacy_IdAndStockIsLessThanEqual(
                        pharmacyId, N, pageable);
        return streamAndReturn(drugs);
    }

    public List<PharmacyDrugDto> searchDrugInPharmacy(
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

    public List<PharmacyDrugDto> getShortageDrugsByPharmacyId(
            Integer pharmacyId, Pageable pageable) {
        Page<PharmacyDrug> drugs =
                pharmacyDrugRepository.getPharmacyDrugsTotalWithShortage(pharmacyId, pageable);
        return streamAndReturn(drugs);
    }

    public List<PharmacyDrugDto> getUnavailableShortageByPharmacyId(
            Integer pharmacyId, Pageable pageable) {
        Page<PharmacyDrug> drugs =
                pharmacyDrugRepository.getUnavailableShortagePharmacyDrugs(pharmacyId, pageable);
        return streamAndReturn(drugs);
    }

    public List<PharmacyDrugDto> getUnavailableDrugsByPharmacyId(
            Integer pharmacyId, Pageable pageable) {
        Page<PharmacyDrug> drugs =
                pharmacyDrugRepository.getUnavailablePharmacyDrugs(pharmacyId, pageable);
        return streamAndReturn(drugs);
    }

    public Integer getTotalStockOfPharmacyDrug(Integer pharmacyId, Integer drugId) {
        List<PharmacyDrug> drugs =
                pharmacyDrugRepository.getAllByPharmacy_IdAndDrug_Id(pharmacyId, drugId);
        return drugs.stream().mapToInt(PharmacyDrug::getStock).reduce(0, Integer::sum);
    }

    public DrugOrderDto getDrugOrderInfoByPharmacyAndDrugIds(Integer pharmacyId, Integer drugId) {
        Optional<DrugOrder> drugOrder =
                drugOrderRepository.getDrugOrderByPharmacy_IdAndDrug_Id(pharmacyId, drugId);
        if (drugOrder.isPresent()) {
            DrugOrderDto info = new DrugOrderDto();
            info.setPharmacy(modelMapper.map(drugOrder.get().getPharmacy(), PharmacyDto.class));
            info.setDrug(modelMapper.map(drugOrder.get().getDrug(), DrugDto.class));
            info.setNOrders(drugOrder.get().getNOrders());
            info.setRequiredAverage(drugOrder.get().getRequired() / drugOrder.get().getNOrders());
            return info;
        }
        return null;
    }

    public List<PharmacyShortageDto> getAllShortageDrugsByPharmacyId(Integer pharmacyId) {
        List<PharmacyDrug> drugs = pharmacyDrugRepository.getAllByPharmacy_Id(pharmacyId);
        Map<DrugOrderId, DrugOrder> drugOrderMap = new HashMap<>();
        Map<DrugOrderId, Integer> totalStock = new HashMap<>();
        List<PharmacyShortageDto> shortageDrugs = new ArrayList<>();
        for (PharmacyDrug pd : drugs) {
            DrugOrderId id = new DrugOrderId(pd.getDrug().getId(), pd.getPharmacy().getId());
            totalStock.computeIfAbsent(
                    id,
                    i ->
                            getTotalStockOfPharmacyDrug(
                                    pd.getPharmacy().getId(), pd.getDrug().getId()));
            DrugOrder order =
                    drugOrderMap.computeIfAbsent(
                            id, i -> drugOrderRepository.getDrugOrderById(id).orElse(null));

            int required = (order != null) ? order.getRequired() : 0;
            if (required > 0) {
                int stock = totalStock.get(id);
                int shortage = Math.max(0, required - stock);
                if (shortage > 0) {
                    PharmacyShortageDto drugShortage = new PharmacyShortageDto();
                    drugShortage.setPharmacy(modelMapper.map(pd.getPharmacy(), PharmacyDto.class));
                    drugShortage.setDrug(modelMapper.map(pd.getDrug(), DrugDto.class));
                    drugShortage.setShortage(shortage);
                    shortageDrugs.add(drugShortage);
                }
            }
        }
        return shortageDrugs;
    }

    public List<PharmacyDrugDto> filter(
            Integer pharmacyId, FilterOption filterOption, Pageable pageable) {
        return switch (filterOption) {
            case AVAILABLE -> getDrugsWithStockOverNByPharmacyId(pharmacyId, 1, pageable);
            case SHORTAGE -> getShortageDrugsByPharmacyId(pharmacyId, pageable);
            case UNAVAILABLE_SHORTAGE -> getUnavailableShortageByPharmacyId(pharmacyId, pageable);
            case UNAVAILABLE -> getUnavailableDrugsByPharmacyId(pharmacyId, pageable);
        };
    }

    public Boolean pharmacyHasDrug(Integer pharmacyId, Integer drugId) {
        return pharmacyDrugRepository.existsPharmacyDrugByPharmacy_IdAndDrug_Id(pharmacyId, drugId);
    }

    public List<PharmacyDto> searchByName(String pharmacyName, Pageable pageable) {
        String formattedName = pharmacyName.trim().toLowerCase().replace(' ', '&');
        return pharmacyRepository
                .searchPharmacyByNamePaginated(pharmacyName, formattedName, pageable)
                .stream()
                .map(ph -> modelMapper.map(ph, PharmacyDto.class))
                .toList();
    }

    @Transactional
    public PharmacyDto addShiftToPharmacy(Integer pharmacyId, Shift shift, User user) {
        shift = shiftService.create(shift);
        Pharmacy pharmacy = getPharmacyByIdOrThrow(pharmacyId);
        PharmacyShift pharmacyShift = new PharmacyShift();
        pharmacyShift.setPharmacy(pharmacy);
        pharmacyShift.setShift(shift);
        pharmacyShiftRepository.save(pharmacyShift);
        return modelMapper.map(getPharmacyByIdOrThrow(pharmacyId), PharmacyDto.class);
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

    public List<EmployeeDto> getEmployeesByPharmacyId(Integer pharmacyId, Pageable pageable) {
        Page<Employee> employees =
                employeeRepository.getEmployeesByPharmacy_Id(pharmacyId, pageable);
        return employees.stream().map(emp -> modelMapper.map(emp, EmployeeDto.class)).toList();
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
