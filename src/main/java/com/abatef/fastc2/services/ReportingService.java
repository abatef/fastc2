package com.abatef.fastc2.services;

import com.abatef.fastc2.dtos.drug.DrugDto;
import com.abatef.fastc2.dtos.pharmacy.SalesOperationDto;
import com.abatef.fastc2.dtos.user.UserDto;
import com.abatef.fastc2.enums.OperationStatus;
import com.abatef.fastc2.enums.OperationType;
import com.abatef.fastc2.enums.UserRole;
import com.abatef.fastc2.exceptions.NotEmployeeException;
import com.abatef.fastc2.exceptions.NotOwnerException;
import com.abatef.fastc2.models.User;
import com.abatef.fastc2.models.pharmacy.Pharmacy;
import com.abatef.fastc2.models.pharmacy.PharmacyDrug;
import com.abatef.fastc2.models.pharmacy.SalesOperation;
import com.abatef.fastc2.repositories.ReceiptRepository;
import com.abatef.fastc2.repositories.SalesOperationsRepository;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ReportingService {
    private final PharmacyService pharmacyService;
    private final ModelMapper modelMapper;
    private final SalesOperationsRepository salesOperationsRepository;

    public ReportingService(
            PharmacyService pharmacyService,
            ReceiptRepository receiptRepository,
            PharmacyService pharmacyService1,
            ModelMapper modelMapper,
            SalesOperationsRepository salesOperationsRepository) {
        this.pharmacyService = pharmacyService1;
        this.modelMapper = modelMapper;
        this.salesOperationsRepository = salesOperationsRepository;
    }

    @PreAuthorize("hasAnyRole('MANAGER', 'OWNER')")
    @Transactional
    public List<SalesOperationDto> getSalesOperations(
            Integer pharmacyId,
            Integer drugId,
            OperationStatus status,
            OperationType type,
            Pageable pageable,
            User user) {
        if (user.getRole() == UserRole.MANAGER) {
            if (!user.getEmployee().getPharmacy().getId().equals(pharmacyId)) {
                throw new NotEmployeeException("this user is not employee");
            }
        }
        if (user.getRole() == UserRole.OWNER) {
            Pharmacy pharmacy = pharmacyService.getPharmacyByIdOrThrow(pharmacyId);
            if (!pharmacy.getOwner().getId().equals(user.getId())) {
                throw new NotOwnerException("this user is not owner");
            }
        }

        if (drugId != null) {
            return salesOperationsRepository
                    .findAllByPharmacy_IdAndDrug_Id(pharmacyId, drugId, pageable)
                    .stream()
                    .map(so -> modelMapper.map(so, SalesOperationDto.class))
                    .toList();
        }

        if (status != null) {
            return salesOperationsRepository
                    .findAllByPharmacy_IdAndStatus(pharmacyId, status, pageable)
                    .stream()
                    .map(so -> modelMapper.map(so, SalesOperationDto.class))
                    .toList();
        }

        if (type != null) {
            return salesOperationsRepository
                    .findAllByPharmacy_IdAndType(pharmacyId, type, pageable)
                    .stream()
                    .map(so -> modelMapper.map(so, SalesOperationDto.class))
                    .toList();
        }

        return salesOperationsRepository.findAllByPharmacy_Id(pharmacyId, pageable);
    }

    public List<SalesOperationDto> applyAllFilters(
            Integer drugId,
            Integer pharmacyId,
            Integer receiptId,
            Integer orderId,
            OperationType type,
            OperationStatus status,
            Integer cashierId,
            Instant fromDate,
            Instant toDate,
            Pageable pageable) {

        Page<SalesOperation> salesOperations =
                salesOperationsRepository.applyAllFilters(
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

        // Map to DTO with calculated profit and revenue
        List<SalesOperationDto> salesOperationDtos =
                salesOperations.getContent().stream()
                        .map(
                                operation -> {
                                    SalesOperationDto dto = new SalesOperationDto();

                                    // Map basic fields
                                    dto.setDrug(
                                            modelMapper.map(operation.getDrug(), DrugDto.class));
                                    dto.setCashier(
                                            modelMapper.map(operation.getCashier(), UserDto.class));
                                    dto.setQuantity(operation.getQuantity());
                                    dto.setType(operation.getType());
                                    dto.setStatus(operation.getStatus());

                                    // Calculate revenue and profit
                                    Float revenue = calculateRevenue(operation);
                                    Float profit = calculateProfit(operation);

                                    dto.setRevenue(revenue);
                                    dto.setProfit(profit);

                                    return dto;
                                })
                        .collect(Collectors.toList());

        return salesOperationDtos;
    }

    /** Calculate revenue for a sales operation based on its type */
    private Float calculateRevenue(SalesOperation operation) {
        // If operation has no quantity, return 0
        if (operation.getQuantity() == null || operation.getQuantity() <= 0) {
            return 0.0f;
        }

        // Handle based on operation type
        switch (operation.getType()) {
            case RECEIPT_ISSUED:
                // For issued receipts, get the actual amount from receipt items
                if (operation.getReceipt() != null) {
                    return operation.getReceipt().getReceiptItems().stream()
                            .filter(
                                    item ->
                                            item.getPharmacyDrug()
                                                    .getDrug()
                                                    .getId()
                                                    .equals(operation.getDrug().getId()))
                            .map(
                                    item -> {
                                        // Calculate total amount considering discount
                                        Float discount =
                                                item.getDiscount() != null
                                                        ? item.getDiscount()
                                                        : 0.0f;
                                        return item.getAmountDue() - discount;
                                    })
                            .reduce(0.0f, Float::sum);
                }
                // Fallback calculation if receipt details aren't available
                return operation.getQuantity() * operation.getDrug().getFullPrice();

            case RECEIPT_REJECTED:
            case RECEIPT_CANCELLED:
            case RECEIPT_RETURNED:
                // For these operations, revenue is negative
                if (operation.getReceipt() != null) {
                    Float receiptRevenue =
                            operation.getReceipt().getReceiptItems().stream()
                                    .filter(
                                            item ->
                                                    item.getPharmacyDrug()
                                                            .getDrug()
                                                            .getId()
                                                            .equals(operation.getDrug().getId()))
                                    .map(
                                            item -> {
                                                Float discount =
                                                        item.getDiscount() != null
                                                                ? item.getDiscount()
                                                                : 0.0f;
                                                return item.getAmountDue() - discount;
                                            })
                                    .reduce(0.0f, Float::sum);
                    return -receiptRevenue; // Negative since it's a reversal
                }
                return -operation.getQuantity() * operation.getDrug().getFullPrice();

            case ORDER_ISSUED:
            case ORDER_COMPLETED:
            case ORDER_CANCELLED:
            case DIRECT_ADDITION:
                // These operations don't generate direct revenue from customers
                return 0.0f;

            default:
                return 0.0f;
        }
    }

    /** Calculate profit for a sales operation based on its type Profit = Revenue - Cost */
    private Float calculateProfit(SalesOperation operation) {
        // If operation has no quantity, return 0
        if (operation.getQuantity() == null || operation.getQuantity() <= 0) {
            return 0.0f;
        }

        // Get the cost of the drug from PharmacyDrug
        Float costPerUnit = 0.0f;
        if (operation.getDrug() != null && operation.getPharmacy() != null) {
            // Find the corresponding PharmacyDrug to get the cost
            Optional<PharmacyDrug> pharmacyDrug =
                    operation.getDrug().getPharmacyDrugs().stream()
                            .filter(
                                    pd ->
                                            pd.getPharmacy()
                                                    .getId()
                                                    .equals(operation.getPharmacy().getId()))
                            .findFirst();

            if (pharmacyDrug.isPresent()) {
                // Assuming PharmacyDrug has a cost field or we can derive cost
                // This part depends on how cost is stored in your system
                costPerUnit =
                        pharmacyDrug.get().getDrug().getFullPrice()
                                / pharmacyDrug.get().getDrug().getUnits();
            }
        }

        Float totalCost = operation.getQuantity() * costPerUnit;
        Float revenue = calculateRevenue(operation);

        // Based on operation type, determine profit
        switch (operation.getType()) {
            case RECEIPT_ISSUED:
                // For sales, profit is revenue minus cost
                return revenue - totalCost;

            case RECEIPT_REJECTED:
            case RECEIPT_CANCELLED:
            case RECEIPT_RETURNED:
                // For returns or cancellations, profit is negative of the original profit
                return revenue + totalCost; // Revenue is already negative here

            case ORDER_ISSUED:
            case ORDER_COMPLETED:
                // For orders, there's typically a cost but no direct revenue
                return -totalCost;

            case ORDER_CANCELLED:
                // For cancelled orders, might have partial refunds or penalties
                // Implementation depends on business rules
                return 0.0f;

            case DIRECT_ADDITION:
                // For inventory additions, there's a cost but no immediate revenue
                return -totalCost;

            default:
                return 0.0f;
        }
    }
}
