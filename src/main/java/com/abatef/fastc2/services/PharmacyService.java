package com.abatef.fastc2.services;

import com.abatef.fastc2.dtos.drug.*;
import com.abatef.fastc2.dtos.pharmacy.Location;
import com.abatef.fastc2.dtos.pharmacy.PharmacyCreationRequest;
import com.abatef.fastc2.dtos.pharmacy.PharmacyDto;
import com.abatef.fastc2.dtos.pharmacy.PharmacyUpdateRequest;
import com.abatef.fastc2.dtos.user.EmployeeDto;
import com.abatef.fastc2.enums.*;
import com.abatef.fastc2.exceptions.NotEmployeeException;
import com.abatef.fastc2.exceptions.NotOwnerException;
import com.abatef.fastc2.exceptions.PharmacyDrugNotFoundException;
import com.abatef.fastc2.exceptions.PharmacyNotFoundException;
import com.abatef.fastc2.models.*;
import com.abatef.fastc2.models.pharmacy.*;
import com.abatef.fastc2.models.shift.PharmacyShift;
import com.abatef.fastc2.models.shift.PharmacyShiftId;
import com.abatef.fastc2.models.shift.Shift;
import com.abatef.fastc2.repositories.*;

import jakarta.validation.constraints.NotNull;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PharmacyService {
    public final Logger LOG = LoggerFactory.getLogger(this.getClass());
    private final PharmacyRepository pharmacyRepository;
    private final DrugService drugService;
    private final ModelMapper modelMapper;
    private final ShiftService shiftService;
    private final PharmacyDrugRepository pharmacyDrugRepository;
    private final PharmacyShiftRepository pharmacyShiftRepository;
    private final EmployeeRepository employeeRepository;
    private final OrderStatsRepository orderStatsRepository;
    private final DrugOrderRepository drugOrderRepository;
    private final OperationRepository operationRepository;

    public PharmacyService(
            PharmacyRepository pharmacyRepository,
            DrugService drugService,
            ModelMapper modelMapper,
            ShiftService shiftService,
            PharmacyDrugRepository pharmacyDrugRepository,
            PharmacyShiftRepository pharmacyShiftRepository,
            EmployeeRepository employeeRepository,
            OrderStatsRepository orderStatsRepository,
            DrugOrderRepository drugOrderRepository,
            OperationRepository operationRepository) {
        this.pharmacyRepository = pharmacyRepository;
        this.drugService = drugService;
        this.modelMapper = modelMapper;
        this.shiftService = shiftService;
        this.pharmacyDrugRepository = pharmacyDrugRepository;
        this.pharmacyShiftRepository = pharmacyShiftRepository;
        this.employeeRepository = employeeRepository;
        this.orderStatsRepository = orderStatsRepository;
        this.drugOrderRepository = drugOrderRepository;
        this.operationRepository = operationRepository;
    }

    @Transactional
    public PharmacyDto createPharmacy(
            PharmacyCreationRequest request, @AuthenticationPrincipal User user) {
        LOG.info("Creating pharmacy");
        Pharmacy pharmacy = new Pharmacy();
        pharmacy.setName(request.getName());
        pharmacy.setAddress(request.getAddress());
        pharmacy.setIsBranch(request.getIsBranch());
        pharmacy.setOwner(user);
        LOG.info("pharmacy owner: {}", user.getUsername());
        if (request.getIsBranch()) {
            LOG.info("is branch");
            Pharmacy mainBranch = getPharmacyByIdOrThrow(request.getMainBranchId());
            LOG.info("main branch: {}", mainBranch.getId());
            pharmacy.setMainBranch(mainBranch);
        }
        pharmacy.setExpiryThreshold(request.getExpiryThreshold());
        if (request.getLocation() != null) {
            pharmacy.setLocation(request.getLocation().toPoint());
        } else {
            pharmacy.setLocation(Location.of(30.1, 32.1).toPoint());
        }
        LOG.info("saving pharmacy");
        pharmacy = pharmacyRepository.save(pharmacy);
        LOG.info("saved pharmacy");
        return modelMapper.map(pharmacy, PharmacyDto.class);
    }

    public Pharmacy getPharmacyByIdOrThrow(Integer id) {
        LOG.info("finding pharmacy by id: {}", id);
        Optional<Pharmacy> pharmacy = pharmacyRepository.findById(id);
        if (pharmacy.isPresent()) {
            LOG.info("found pharmacy with name: {}", pharmacy.get().getName());
            return pharmacy.get();
        }
        LOG.error("pharmacy not found, throwing exception");
        throw new PharmacyNotFoundException(id);
    }

    public PharmacyDto getPharmacyInfoById(Integer id, User user) {
        LOG.info("trying to get pharmacy info by id: {}", id);
        if (user.getRole() == UserRole.EMPLOYEE) {
            LOG.info("employee role");
            if (user.getEmployee().getPharmacy().getId().equals(id)) {
                return modelMapper.map(getPharmacyByIdOrThrow(id), PharmacyDto.class);
            }
            throw new NotEmployeeException("user is not a employee");
        }
        if (user.getRole() == UserRole.MANAGER) {
            LOG.info("manager role");
            Pharmacy pharmacy = getPharmacyByIdOrThrow(id);
            if (pharmacy.getOwner().getId().equals(user.getId())) {
                return modelMapper.map(pharmacy, PharmacyDto.class);
            }
        }
        throw new NotOwnerException("user is not a manager");
    }

    public List<PharmacyDto> getAllPharmacies(Pageable pageable) {
        LOG.info("trying to get all pharmacies");
        return pharmacyRepository.findAll(pageable).stream()
                .map(ph -> modelMapper.map(ph, PharmacyDto.class))
                .toList();
    }

    @PreAuthorize("hasRole('MANAGER')")
    @Transactional
    public void deletePharmacyById(Integer id, User user) {
        Pharmacy ph = getPharmacyByIdOrThrow(id);
        if (ph != null) {
            if (ph.getOwner().getId().equals(user.getId())) {
                pharmacyRepository.deleteById(id);
                return;
            }
            throw new NotOwnerException("user is not owner of pharmacy");
        }
    }

    @PreAuthorize("hasRole('MANAGER')")
    @Transactional
    public PharmacyDto updatePharmacyInfo(
            @NotNull PharmacyUpdateRequest pharmacyInfo, @AuthenticationPrincipal User user) {
        LOG.info("updating pharmacy info");
        Pharmacy pharmacy = getPharmacyByIdOrThrow(pharmacyInfo.getId());
        managerOrEmployeeOrThrow(user, pharmacy);
        if (!Objects.equals(pharmacy.getOwner().getId(), user.getId())) {
            throw new NotOwnerException("user is not owner of pharmacy");
        }
        boolean isUpdated = false;
        if (pharmacyInfo.getName() != null && !pharmacyInfo.getName().isEmpty()) {
            LOG.info(
                    "updating pharmacy name, old: {} new: {}",
                    pharmacy.getName(),
                    pharmacyInfo.getName());
            pharmacy.setName(pharmacyInfo.getName());
            isUpdated = true;
        }

        if (pharmacyInfo.getAddress() != null && !pharmacyInfo.getAddress().isEmpty()) {
            LOG.info(
                    "updating pharmacy address, old: {} new: {}",
                    pharmacy.getAddress(),
                    pharmacyInfo.getAddress());
            pharmacy.setAddress(pharmacyInfo.getAddress());
            isUpdated = true;
        }

        if (pharmacyInfo.getLocation() != null) {
            LOG.info(
                    "updating pharmacy location, old: {} new: {}",
                    Location.of(pharmacy.getLocation()),
                    pharmacyInfo.getLocation());
            pharmacy.setLocation(pharmacyInfo.getLocation().toPoint());
            isUpdated = true;
        }

        if (pharmacyInfo.getExpiryThreshold() != null) {
            LOG.info(
                    "updating pharmacy expiry threshold, old: {} new: {}",
                    pharmacy.getExpiryThreshold(),
                    pharmacyInfo.getExpiryThreshold());
            pharmacy.setExpiryThreshold(pharmacyInfo.getExpiryThreshold());
            isUpdated = true;
        }
        if (isUpdated) {
            LOG.info("saved new updates");
            pharmacy = pharmacyRepository.save(pharmacy);
        } else {
            LOG.info("no updates to save");
        }
        return modelMapper.map(pharmacy, PharmacyDto.class);
    }

    @PreAuthorize("hasAnyRole('EMPLOYEE', 'MANAGER')")
    @Transactional
    public PharmacyDrugDto addDrugToPharmacy(
            PharmacyDrugCreationRequest request, Integer pharmacyId, User user) {
        Pharmacy pharmacy = getPharmacyByIdOrThrow(pharmacyId);
        managerOrEmployeeOrThrow(user, pharmacy);
        Drug drug = drugService.getDrugByIdOrThrow(request.getDrugId());
        PharmacyDrug pharmacyDrug = new PharmacyDrug();
        pharmacyDrug.setDrug(drug);
        pharmacyDrug.setPharmacy(pharmacy);
        pharmacyDrug.setExpiryDate(request.getExpiryDate());
        pharmacyDrug.setPrice(drug.getFullPrice());
        pharmacyDrug.setStock(request.getStock() * drug.getUnits());
        pharmacyDrug.setAddedBy(user);
        pharmacyDrug = pharmacyDrugRepository.save(pharmacyDrug);
        OrderStatsId id = new OrderStatsId(drug.getId(), pharmacy.getId());
        Optional<OrderStats> drugOrderOptional = orderStatsRepository.getDrugOrderById(id);
        OrderStats order;
        if (drugOrderOptional.isPresent()) {
            order = drugOrderOptional.get();
            Integer oldRequired = order.getRequired();
            Integer oldNOrders = order.getNOrders();
            order.setNOrders(oldNOrders + 1);
            order.setRequired(oldRequired + request.getStock());
        } else {
            order = new OrderStats();
            order.setId(id);
            order.setDrug(drug);
            order.setPharmacy(pharmacy);
            order.setRequired(request.getStock());
            order.setNOrders(1);
        }
        orderStatsRepository.save(order);
        return modelMapper.map(pharmacyDrug, PharmacyDrugDto.class);
    }

    public PharmacyDrug getPharmacyDrugByIdOrThrow(Integer id) {
        Optional<PharmacyDrug> pharmacyDrug = pharmacyDrugRepository.findById(id);
        if (pharmacyDrug.isPresent()) {
            return pharmacyDrug.get();
        }
        throw new PharmacyDrugNotFoundException(id);
    }

    public void managerOrEmployeeOrThrow(User user, Integer pharmacyId) {
        if (user.getRole() == UserRole.MANAGER) {
            Pharmacy pharmacy = getPharmacyByIdOrThrow(pharmacyId);
            if (pharmacy.getOwner().getId().equals(user.getId())) {
                return;
            }
            throw new NotOwnerException("user is not owner of pharmacy");
        }
        if (user.getRole() == UserRole.EMPLOYEE) {
            if (user.getEmployee().getPharmacy().getId().equals(pharmacyId)) {
                return;
            }
            throw new NotEmployeeException("user is not employee in this pharmacy");
        }
    }

    public void managerOrEmployeeOrThrow(User user, Pharmacy pharmacy) {
        if (user.getRole() == UserRole.MANAGER) {
            if (pharmacy.getOwner().getId().equals(user.getId())) {
                return;
            }
            throw new NotOwnerException("user is not owner of pharmacy");
        }
        if (user.getRole() == UserRole.EMPLOYEE) {
            if (user.getEmployee().getPharmacy().getId().equals(pharmacy.getId())) {
                return;
            }
            throw new NotEmployeeException("user is not employee in this pharmacy");
        }
    }

    public PharmacyDrugDto getPharmacyDrugInfoById(Integer id, User user) {
        PharmacyDrug pd = getPharmacyDrugByIdOrThrow(id);
        managerOrEmployeeOrThrow(user, pd.getPharmacy().getId());
        return modelMapper.map(pd, PharmacyDrugDto.class);
    }

    @PreAuthorize("hasRole('MANAGER')")
    @Transactional
    public void removeDrugFromPharmacy(Integer id) {
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
        return drugs.stream().min(Comparator.comparing(PharmacyDrug::getExpiryDate)).orElse(null);
    }


    public List<PharmacyDrugDto> getDrugsByPharmacyId(
            Integer pharmacyId, Pageable pageable, User user) {
        managerOrEmployeeOrThrow(user, pharmacyId);
        Page<PharmacyDrug> drugs =
                pharmacyDrugRepository.getPharmacyDrugsByPharmacy_Id(pharmacyId, pageable);
        return streamAndReturn(drugs);
    }

    public List<PharmacyDrug> searchDrugInPharmacy(
            String drugName, Integer pharmacyId, Pageable pageable) {
        String formattedName = drugName.trim().toLowerCase().replace(' ', '&');
        Page<PharmacyDrug> pharmacyDrugs =
                pharmacyDrugRepository.searchByDrugName(
                        pharmacyId, drugName, formattedName, pageable);
        if (pharmacyDrugs.isEmpty()) {
            return List.of();
        }
        return pharmacyDrugs.getContent();
    }

    public Integer getTotalStockOfPharmacyDrug(Integer pharmacyId, Integer drugId) {
        LOG.info("getting total drugs stock for pharmacy: {}, drug: {}", pharmacyId, drugId);
        List<PharmacyDrug> drugs =
                pharmacyDrugRepository.getAllByPharmacy_IdAndDrug_Id(pharmacyId, drugId);
        Integer totalStock =
                drugs.stream().mapToInt(PharmacyDrug::getStock).reduce(0, Integer::sum);
        LOG.info("total stock: {}", totalStock);
        return totalStock;
    }

    public DrugStatsDto getDrugOrderInfoByPharmacyAndDrugIds(
            Integer pharmacyId, Integer drugId, User user) {
        managerOrEmployeeOrThrow(user, pharmacyId);
        Optional<OrderStats> drugOrder =
                orderStatsRepository.getDrugOrderByPharmacy_IdAndDrug_Id(pharmacyId, drugId);
        if (drugOrder.isPresent()) {
            DrugStatsDto info = new DrugStatsDto();
            info.setPharmacy(modelMapper.map(drugOrder.get().getPharmacy(), PharmacyDto.class));
            info.setDrug(modelMapper.map(drugOrder.get().getDrug(), DrugDto.class));
            info.setNOrders(drugOrder.get().getNOrders());
            info.setRequiredAverage(drugOrder.get().getRequired() / drugOrder.get().getNOrders());
            return info;
        }
        return null;
    }

    public List<PharmacyShortageDto> getAllShortageDrugsByPharmacyId(
            Integer pharmacyId, Pageable pageable, User user) {
        managerOrEmployeeOrThrow(user, pharmacyId);
        Map<OrderStatsId, OrderStats> drugOrderMap = new HashMap<>();
        Map<OrderStatsId, Integer> totalStock = new HashMap<>();
        List<PharmacyShortageDto> shortageDrugs = new ArrayList<>();
        LOG.info("fetching first page....");
        List<PharmacyDrug> drugs =
                pharmacyDrugRepository
                        .getPharmacyDrugsByPharmacy_Id(pharmacyId, pageable)
                        .getContent();
        LOG.info("done fetching first page....");
        while (shortageDrugs.size() < pageable.getPageSize()) {
            if (drugs.isEmpty()) {
                LOG.info("empty drugs, stopping....");
                break;
            }
            for (PharmacyDrug pd : drugs) {
                OrderStatsId id = new OrderStatsId(pd.getDrug().getId(), pd.getPharmacy().getId());
                if (totalStock.containsKey(id)) {
                    LOG.info("duplicate drug order id, skipping....");
                    continue;
                }
                totalStock.computeIfAbsent(
                        id,
                        i ->
                                getTotalStockOfPharmacyDrug(
                                        pd.getPharmacy().getId(), pd.getDrug().getId()));
                OrderStats order =
                        drugOrderMap.computeIfAbsent(
                                id, i -> orderStatsRepository.getDrugOrderById(id).orElse(null));

                int required = (order != null) ? order.getRequired() : 0;
                LOG.info("requiring {} of stock", required);
                if (required > 0) {
                    int stock = totalStock.get(id);
                    LOG.info("total stock found: {}", stock);
                    int shortage = Math.max(0, required - stock);
                    LOG.info("shortage found: {}", shortage);
                    if (shortage > 0) {
                        PharmacyShortageDto drugShortage = new PharmacyShortageDto();
                        drugShortage.setPharmacy(
                                modelMapper.map(pd.getPharmacy(), PharmacyDto.class));
                        drugShortage.setDrug(modelMapper.map(pd.getDrug(), DrugDto.class));
                        drugShortage.setShortage(shortage);
                        shortageDrugs.add(drugShortage);
                        if (shortageDrugs.size() == pageable.getPageSize()) {
                            return shortageDrugs;
                        }
                    }
                }
            }
            LOG.info("fetching another page...");
            pageable = pageable.next();
            drugs =
                    pharmacyDrugRepository
                            .getPharmacyDrugsByPharmacy_Id(pharmacyId, pageable)
                            .getContent();
            LOG.info("done fetching page...");
        }
        LOG.info("total shortage: {}", shortageDrugs.size());
        return shortageDrugs;
    }

    @PreAuthorize("hasAnyRole('MANAGER', 'EMPLOYEE')")
    public List<PharmacyDrugDto> applyAllFilters(
            Integer pharmacyId,
            Integer drugId,
            String query,
            List<FilterOption> filterOptions,
            SortOption sortOption,
            Integer N,
            Float upperPriceBound,
            String type,
            Pageable pageable,
            User user) {
        LOG.info("applying all filters");
        Pharmacy pharmacy = getPharmacyByIdOrThrow(pharmacyId);
        managerOrEmployeeOrThrow(user, pharmacy);
        Page<PharmacyDrug> drugs =
                pharmacyDrugRepository.getPharmacyDrugsByPharmacy_Id(pharmacyId, pageable);
        LOG.info("found {} drugs", drugs.getTotalElements());
        List<PharmacyDrug> drugsList = drugs.getContent();
        List<PharmacyDrug> filteredDrugs;
        if (query != null && !query.isEmpty()) {
            LOG.info("search term: {}", query);
            filteredDrugs = searchDrugInPharmacy(query, pharmacyId, pageable);
        } else {
            filteredDrugs = new ArrayList<>(drugsList);
        }

        LocalDate today = LocalDate.now();

        if (filterOptions == null) {
            LOG.info("filter options is null");
            filterOptions = new ArrayList<>();
        }

        for (FilterOption option : filterOptions) {
            LOG.info("filter option: {}", option.name());
            switch (option) {
                case AVAILABLE:
                    filteredDrugs =
                            filteredDrugs.stream()
                                    .filter(drug -> drug.getStock() > 0)
                                    .collect(Collectors.toList());
                    break;

                case SHORTAGE:
                    filteredDrugs =
                            filteredDrugs.stream()
                                    .filter(
                                            drug -> {
                                                OrderStatsId id =
                                                        new OrderStatsId(
                                                                drug.getDrug().getId(), pharmacyId);
                                                Optional<OrderStats> orderOptional =
                                                        orderStatsRepository.getDrugOrderById(id);
                                                if (orderOptional.isPresent()) {
                                                    OrderStats order = orderOptional.get();
                                                    return drug.getStock() > 0
                                                            && drug.getStock()
                                                                    < order.getRequired();
                                                }
                                                return false;
                                            })
                                    .collect(Collectors.toList());
                    break;

                case UNAVAILABLE_SHORTAGE:
                    filteredDrugs =
                            filteredDrugs.stream()
                                    .filter(
                                            drug -> {
                                                OrderStatsId id =
                                                        new OrderStatsId(
                                                                drug.getDrug().getId(), pharmacyId);
                                                Optional<OrderStats> orderOptional =
                                                        orderStatsRepository.getDrugOrderById(id);
                                                if (orderOptional.isPresent()) {
                                                    OrderStats order = orderOptional.get();
                                                    return drug.getStock() == 0
                                                            && order.getRequired() > 0;
                                                }
                                                return false;
                                            })
                                    .collect(Collectors.toList());
                    break;

                case UNAVAILABLE:
                    filteredDrugs =
                            filteredDrugs.stream()
                                    .filter(
                                            drug -> {
                                                OrderStatsId id =
                                                        new OrderStatsId(
                                                                drug.getDrug().getId(), pharmacyId);
                                                Optional<OrderStats> orderOptional =
                                                        orderStatsRepository.getDrugOrderById(id);
                                                if (orderOptional.isPresent()) {
                                                    OrderStats order = orderOptional.get();
                                                    return drug.getStock() == 0
                                                            && order.getRequired() == 0;
                                                } else {
                                                    return drug.getStock() == 0;
                                                }
                                            })
                                    .collect(Collectors.toList());
                    break;

                case EXPIRES_AFTER_N:
                    LocalDate dateAfterN = today.plusDays(N);
                    filteredDrugs =
                            filteredDrugs.stream()
                                    .filter(drug -> drug.getExpiryDate().isAfter(dateAfterN))
                                    .collect(Collectors.toList());
                    break;

                case STOCK_OVER_N:
                    filteredDrugs =
                            filteredDrugs.stream()
                                    .filter(drug -> drug.getStock() > N)
                                    .collect(Collectors.toList());
                    break;

                case STOCK_UNDER_N:
                    filteredDrugs =
                            filteredDrugs.stream()
                                    .filter(drug -> drug.getStock() < N && drug.getStock() > 0)
                                    .collect(Collectors.toList());
                    break;

                case OUT_OF_STOCK:
                    filteredDrugs =
                            filteredDrugs.stream()
                                    .filter(drug -> drug.getStock() == 0)
                                    .collect(Collectors.toList());
                    break;

                case EXPIRED:
                    filteredDrugs =
                            filteredDrugs.stream()
                                    .filter(
                                            drug ->
                                                    drug.getExpiryDate().isBefore(today)
                                                            || drug.getExpiryDate().isEqual(today))
                                    .collect(Collectors.toList());
                    break;

                case APPROACHING_EXPIRY:
                    LocalDate approachingDate = today.plusDays(pharmacy.getExpiryThreshold());
                    filteredDrugs =
                            filteredDrugs.stream()
                                    .filter(
                                            drug ->
                                                    drug.getExpiryDate().isAfter(today)
                                                            && (drug.getExpiryDate()
                                                                            .isBefore(
                                                                                    approachingDate)
                                                                    || drug.getExpiryDate()
                                                                            .isEqual(
                                                                                    approachingDate)))
                                    .collect(Collectors.toList());
                    break;

                case NOT_EXPIRED:
                    filteredDrugs =
                            filteredDrugs.stream()
                                    .filter(drug -> drug.getExpiryDate().isAfter(today))
                                    .collect(Collectors.toList());
                    break;

                case BY_FORM:
                    filteredDrugs =
                            filteredDrugs.stream()
                                    .filter(
                                            drug ->
                                                    drug.getDrug().getForm() != null
                                                            && drug.getDrug()
                                                                    .getForm()
                                                                    .equalsIgnoreCase(type))
                                    .collect(Collectors.toList());
                    break;

                case PRICE_BELOW_N:
                    filteredDrugs =
                            filteredDrugs.stream()
                                    .filter(drug -> drug.getPrice() < N)
                                    .collect(Collectors.toList());
                    break;

                case PRICE_ABOVE_N:
                    filteredDrugs =
                            filteredDrugs.stream()
                                    .filter(drug -> drug.getPrice() > N)
                                    .collect(Collectors.toList());
                    break;

                case PRICE_BETWEEN:
                    filteredDrugs =
                            filteredDrugs.stream()
                                    .filter(
                                            drug ->
                                                    drug.getPrice() >= N
                                                            && (upperPriceBound == null
                                                                    || drug.getPrice()
                                                                            <= upperPriceBound))
                                    .collect(Collectors.toList());
                    break;

                case DISCOUNTED:
                    filteredDrugs =
                            filteredDrugs.stream()
                                    .filter(
                                            drug ->
                                                    drug.getReceiptItems().stream()
                                                            .anyMatch(
                                                                    item ->
                                                                            item.getDiscount()
                                                                                            != null
                                                                                    && item
                                                                                                    .getDiscount()
                                                                                            > 0))
                                    .collect(Collectors.toList());

                    break;
            }
        }

        if (drugId != null) {
            LOG.info("filtering by drug: {}", drugId);
            filteredDrugs =
                    filteredDrugs.stream()
                            .filter(drug -> drug.getDrug().getId().equals(drugId))
                            .collect(Collectors.toList());
        }

        if (sortOption != null) {
            LOG.info("sort option: {}", sortOption);
            switch (sortOption) {
                case EXPIRY_DATE_ASC:
                    filteredDrugs.sort(Comparator.comparing(PharmacyDrug::getExpiryDate));
                    break;

                case EXPIRY_DATE_DESC:
                    filteredDrugs.sort(
                            Comparator.comparing(PharmacyDrug::getExpiryDate).reversed());
                    break;

                case PRICE_ASC:
                    filteredDrugs.sort(Comparator.comparing(PharmacyDrug::getPrice));
                    break;

                case PRICE_DESC:
                    filteredDrugs.sort(Comparator.comparing(PharmacyDrug::getPrice).reversed());
                    break;

                case STOCK_ASC:
                    filteredDrugs.sort(Comparator.comparing(PharmacyDrug::getStock));
                    break;

                case STOCK_DESC:
                    filteredDrugs.sort(Comparator.comparing(PharmacyDrug::getStock).reversed());
                    break;
            }
        }
        LOG.info("done filtering.");
        return filteredDrugs.stream()
                .map(drug -> modelMapper.map(drug, PharmacyDrugDto.class))
                .collect(Collectors.toList());
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
        Pharmacy pharmacy = getPharmacyByIdOrThrow(pharmacyId);
        managerOrEmployeeOrThrow(user, pharmacy);
        shift = shiftService.create(shift);
        PharmacyShiftId id = new PharmacyShiftId();
        id.setPharmacyId(pharmacyId);
        id.setShiftId(shift.getId());
        PharmacyShift pharmacyShift = new PharmacyShift();
        pharmacyShift.setId(id);
        pharmacyShift.setPharmacy(pharmacy);
        pharmacyShift.setShift(shift);
        pharmacyShiftRepository.save(pharmacyShift);
        return modelMapper.map(getPharmacyByIdOrThrow(pharmacyId), PharmacyDto.class);
    }

    @PreAuthorize("hasRole('MANAGER')")
    @Transactional
    public void removeShiftFromPharmacy(Integer pharmacyId, Integer shiftId, User user) {
        managerOrEmployeeOrThrow(user, pharmacyId);
        PharmacyShiftId id = new PharmacyShiftId();
        id.setPharmacyId(pharmacyId);
        id.setShiftId(shiftId);
        pharmacyShiftRepository.deleteById(id);
    }

    @PreAuthorize("hasRole('MANAGER')")
    public List<Shift> getShiftsByPharmacyId(Integer pharmacyId, User user) {
        Pharmacy pharmacy = getPharmacyByIdOrThrow(pharmacyId);
        managerOrEmployeeOrThrow(user, pharmacy);
        return pharmacy.getShifts().stream().toList();
    }

    @PreAuthorize("hasRole('MANAGER')")
    @Transactional
    public List<EmployeeDto> getEmployeesByPharmacyId(
            Integer pharmacyId, EmployeeStatus status, Pageable pageable, User user) {
        managerOrEmployeeOrThrow(user, pharmacyId);
        Page<Employee> employees;
        if (status == EmployeeStatus.INACTIVE) {
            employees =
                    employeeRepository.findEmployeesByEndDateIsNotNullAndPharmacy_Id(
                            pharmacyId, pageable);
        } else if (status == EmployeeStatus.ACTIVE) {
            employees =
                    employeeRepository.findEmployeesByEndDateIsNullAndPharmacy_Id(
                            pharmacyId, pageable);
        } else {
            employees = employeeRepository.findEmployeesByPharmacy_Id(pharmacyId, pageable);
        }
        return employees.stream().map(emp -> modelMapper.map(emp, EmployeeDto.class)).toList();
    }

    @PreAuthorize("hasRole('MANAGER')")
    @Transactional
    public void removeEmployeeFromPharmacy(Integer pharmacyId, Integer employeeId, User user) {
        managerOrEmployeeOrThrow(user, pharmacyId);
        Optional<Employee> employee =
                employeeRepository.getEmployeeByIdAndPharmacy_Id(employeeId, pharmacyId);
        if (employee.isPresent()) {
            Employee employeeToRemove = employee.get();
            employeeToRemove.setEndDate(LocalDate.now());
            employeeRepository.save(employeeToRemove);
        }
    }

    @Transactional
    public void createOperation(User user, OperationType type, DrugOrder order) {
        Operation operation = new Operation();
        operation.setType(type);
        operation.setCashier(user);
        operation.setOrder(order);
        operationRepository.save(operation);
    }

    @Transactional
    public DrugOrderDto orderDrug(OrderCreationRequest request, Integer pharmacyId, User user) {
        Pharmacy pharmacy = getPharmacyByIdOrThrow(pharmacyId);
        managerOrEmployeeOrThrow(user, pharmacy);
        DrugOrder order = new DrugOrder();
        order.setName(request.getName());
        order.setOrderedBy(user);
        order.setPharmacy(pharmacy);
        order.setStatus(OrderStatus.ISSUED);
        order = drugOrderRepository.save(order);
        float orderTotal = 0.0f;
        for (OrderItemRequest item : request.getItems()) {
            OrderItemId id = new OrderItemId();
            id.setOrderId(order.getId());
            id.setDrugId(item.getDrugId());
            OrderItem orderItem = new OrderItem();
            orderItem.setId(id);
            orderItem.setRequired(item.getRequired());
            Drug drug = drugService.getDrugByIdOrThrow(item.getDrugId());
            orderItem.setDrug(drug);
            orderTotal += drug.getFullPrice() * item.getRequired();
            order.getOrderItems().add(orderItem);
        }
        order = drugOrderRepository.save(order);
        createOperation(user, OperationType.ORDER_ISSUED, order);
        DrugOrderDto dto = modelMapper.map(order, DrugOrderDto.class);
        dto.setOrderTotal(orderTotal);
        return dto;
    }

    @Transactional
    public DrugOrderDto changeOrderStatus(
            Integer pharmacyId, Integer orderId, OrderStatus orderStatus, User user) {
        managerOrEmployeeOrThrow(user, pharmacyId);
        DrugOrder order = drugOrderRepository.getDrugOrderByIdAndPharmacy_Id(orderId, pharmacyId);
        order.setStatus(orderStatus);
        order = drugOrderRepository.save(order);

        if (orderStatus == OrderStatus.CANCELLED) {
            createOperation(user, OperationType.ORDER_CANCELLED, order);
        } else if (orderStatus == OrderStatus.COMPLETED
                || orderStatus == OrderStatus.PARTIAL_COMPLETION) {
            createOperation(user, OperationType.ORDER_COMPLETED, order);
        }
        return modelMapper.map(order, DrugOrderDto.class);
    }

    public List<DrugOrderDto> getAllOrders(
            Integer pharmacyId, Integer drugId, Integer userId, Pageable pageable, User user) {
        managerOrEmployeeOrThrow(user, pharmacyId);
        Page<DrugOrder> orders =
                drugOrderRepository.findDrugOrdersFiltered(drugId, pharmacyId, userId, pageable);
        if (orders.getTotalElements() == 0) {
            return Collections.emptyList();
        }
        return orders.stream().map(order -> modelMapper.map(order, DrugOrderDto.class)).toList();
    }
}
