package com.abatef.fastc2.services;

import com.abatef.fastc2.dtos.receipt.ReceiptCreationRequest;
import com.abatef.fastc2.dtos.receipt.ReceiptDto;
import com.abatef.fastc2.dtos.receipt.ReceiptItemDto;
import com.abatef.fastc2.enums.ItemStatus;
import com.abatef.fastc2.enums.OperationType;
import com.abatef.fastc2.enums.ReceiptStatus;
import com.abatef.fastc2.enums.UserRole;
import com.abatef.fastc2.exceptions.NotEmployeeException;
import com.abatef.fastc2.exceptions.PharmacyDrugNotFoundException;
import com.abatef.fastc2.exceptions.ReceiptNotFoundException;
import com.abatef.fastc2.models.User;
import com.abatef.fastc2.models.pharmacy.*;
import com.abatef.fastc2.repositories.OperationRepository;
import com.abatef.fastc2.repositories.PharmacyDrugRepository;
import com.abatef.fastc2.repositories.ReceiptItemRepository;
import com.abatef.fastc2.repositories.ReceiptRepository;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;

@Service
public class ReceiptService {
    /*
     * filter -> date, shifts, custom time(shift), employee(may be multiple)
     * filter by drugs
     * */

    private final ReceiptRepository receiptRepository;
    private final PharmacyService pharmacyService;
    private final ModelMapper modelMapper;
    private final OperationRepository operationRepository;
    private final PharmacyDrugRepository pharmacyDrugRepository;
    private final ReceiptItemRepository receiptItemRepository;

    public ReceiptService(
            ReceiptRepository receiptRepository,
            PharmacyService pharmacyService,
            ModelMapper modelMapper,
            OperationRepository operationRepository,
            PharmacyDrugRepository pharmacyDrugRepository,
            ReceiptItemRepository receiptItemRepository) {
        this.receiptRepository = receiptRepository;
        this.pharmacyService = pharmacyService;
        this.modelMapper = modelMapper;
        this.operationRepository = operationRepository;
        this.pharmacyDrugRepository = pharmacyDrugRepository;
        this.receiptItemRepository = receiptItemRepository;
    }

    private static Receipt getReceipt(Integer id, Optional<Receipt> receiptOptional) {
        Receipt receipt = receiptOptional.orElseThrow(() -> new ReceiptNotFoundException(id));
        if (receipt.getStatus() == ReceiptStatus.RETURNED) {
            throw new IllegalStateException("receipt is already returned");
        }

        if (receipt.getStatus() != ReceiptStatus.ISSUED
                && receipt.getStatus() != ReceiptStatus.PARTIALLY_FULFILLED) {
            throw new IllegalStateException(
                    "can not return receipt with status: " + receipt.getStatus().name());
        }
        return receipt;
    }

    @PreAuthorize("hasAnyRole('EMPLOYEE', 'MANAGER')")
    @Transactional
    public ReceiptDto createANewReceipt(
            List<ReceiptCreationRequest> requests, Integer pharmacyId, User cashier) {
        if (cashier.getRole() == UserRole.EMPLOYEE || cashier.getRole() == UserRole.MANAGER) {
            if (!cashier.getEmployee().getPharmacy().getId().equals(pharmacyId)) {
                throw new NotEmployeeException("user is not employee in this pharmacy");
            }
        }
        Receipt receipt = new Receipt();
        receipt.setCashier(cashier);
        receipt.setStatus(ReceiptStatus.ISSUED);
        receipt.setShift(cashier.getEmployee().getShift());
        receipt = receiptRepository.save(receipt);
        Operation operation = new Operation();
        operation.setCashier(cashier);
        operation.setReceipt(receipt);
        Set<ReceiptItem> items = new HashSet<>();
        for (ReceiptCreationRequest request : requests) {
            PharmacyDrug drug = pharmacyService.getNextDrugToSell(pharmacyId, request.getDrugId());
            if (drug == null) {
                throw new PharmacyDrugNotFoundException(
                        pharmacyId,
                        request.getDrugId(),
                        PharmacyDrugNotFoundException.Why.NONEXISTENT_DRUG_PHARMACY);
            }
            ReceiptItem item = new ReceiptItem();
            item.setReceipt(receipt);
            item.setPharmacyDrug(drug);
            item.setStatus(ItemStatus.SATISFIED);
            int requiredQuantity = 0;
            if (request.getUnits() != null && request.getUnits() > 0) {
                requiredQuantity += request.getUnits();
            }
            if (request.getPacks() != null && request.getPacks() > 0) {
                requiredQuantity += request.getPacks() * drug.getDrug().getUnits();
            }
            int remainingQuantity = requiredQuantity;
            Map<PharmacyDrug, Integer> drugQuantities = new HashMap<>();
            while (remainingQuantity > 0 && drug != null) {
                int drugStock = Math.min(remainingQuantity, drug.getStock());
                if (drugStock > 0) {
                    drugQuantities.put(drug, drugStock);
                    remainingQuantity -= drugStock;
                }
                if (remainingQuantity > 0) {
                    drug = pharmacyService.getNextDrugToSell(pharmacyId, request.getDrugId());
                }
            }

            if (remainingQuantity > 0) {
                operation.setType(OperationType.RECEIPT_REJECTED);
                operationRepository.save(operation);
                item.setStatus(ItemStatus.UNSATISFIED);
            }

            int satisfiedItems = 0;

            if (item.getStatus() == ItemStatus.SATISFIED) {
                float amountDue = 0;
                for (Map.Entry<PharmacyDrug, Integer> entry : drugQuantities.entrySet()) {
                    PharmacyDrug pd = entry.getKey();
                    float pricePerUnit = pd.getPrice() / pd.getDrug().getUnits();
                    amountDue += pricePerUnit * entry.getValue();
                    pd.setStock(pd.getStock() - entry.getValue());
                    pharmacyDrugRepository.save(pd);
                }
                item.setAmountDue(amountDue);
                operation.setType(OperationType.RECEIPT_ISSUED);
                operationRepository.save(operation);
                ++satisfiedItems;
            }

            if (satisfiedItems == 0) {
                receipt.setStatus(ReceiptStatus.REJECTED);
            } else if (satisfiedItems < requests.size()) {
                receipt.setStatus(ReceiptStatus.PARTIALLY_FULFILLED);
            } else {
                receipt.setStatus(ReceiptStatus.REJECTED);
            }

            item.setPack(request.getPacks());
            item.setUnits(request.getUnits());
            ReceiptItemId id = new ReceiptItemId();
            id.setReceiptId(receipt.getId());
            id.setPharmacyDrugId(pharmacyId);
            item.setId(id);
            receiptItemRepository.save(item);
        }

        ReceiptDto info = modelMapper.map(receipt, ReceiptDto.class);
        float revenue = 0.0f;
        float total = 0.0f;
        info.setTotal(total);
        List<ReceiptItem> receiptItems =
                receiptItemRepository.findReceiptItemsByReceipt_Id(receipt.getId());
        for (ReceiptItem receiptItem : receiptItems) {
            ReceiptItemDto itemInfo = new ReceiptItemDto();
            itemInfo.setDrugName(receiptItem.getPharmacyDrug().getDrug().getName());
            itemInfo.setDiscount(receiptItem.getDiscount());
            itemInfo.setPack(receiptItem.getPack());
            itemInfo.setUnits(receiptItem.getUnits());
            itemInfo.setAmountDue(receiptItem.getAmountDue());
            itemInfo.setStatus(receiptItem.getStatus());
            info.setTotal(info.getTotal() + receiptItem.getAmountDue());
            info.getItems().add(itemInfo);
            revenue += receiptItem.getAmountDue();
            total += receiptItem.getPharmacyDrug().getDrug().getFullPrice();
        }
        info.setRevenue(revenue);
        info.setProfit(info.getRevenue() - total);
        return info;
    }

    private ReceiptDto mapReceiptDto(Receipt receipt) {
        ReceiptDto info = modelMapper.map(receipt, ReceiptDto.class);
        float revenue = 0.0f;
        float total = 0.0f;
        for (ReceiptItem receiptItem : receipt.getReceiptItems()) {
            ReceiptItemDto itemInfo = new ReceiptItemDto();
            itemInfo.setDrugName(receiptItem.getPharmacyDrug().getDrug().getName());
            itemInfo.setDiscount(receiptItem.getDiscount());
            itemInfo.setPack(receiptItem.getPack());
            itemInfo.setUnits(receiptItem.getUnits());
            itemInfo.setAmountDue(receiptItem.getAmountDue());
            itemInfo.setStatus(receiptItem.getStatus());
            info.setTotal(info.getTotal() + receiptItem.getAmountDue());
            info.getItems().add(itemInfo);
            revenue += receiptItem.getAmountDue();
            total += receiptItem.getPharmacyDrug().getDrug().getFullPrice();
        }
        info.setRevenue(revenue);
        info.setProfit(info.getRevenue() - total);
        return info;
    }

    public Receipt getReceiptByIdOrThrow(Integer id) {
        Optional<Receipt> receipt = receiptRepository.findById(id);
        if (receipt.isPresent()) {
            return receipt.get();
        }
        throw new ReceiptNotFoundException(id);
    }

    public ReceiptDto getReceiptInfoById(Integer id) {
        return modelMapper.map(getReceiptByIdOrThrow(id), ReceiptDto.class);
    }

    private List<ReceiptDto> streamAndMap(List<Receipt> receipts) {
        return receipts.stream()
                .map(receipt -> modelMapper.map(receipt, ReceiptDto.class))
                .toList();
    }

    @Transactional
    public ReceiptDto returnReceipt(Integer id, User cashier) {
        Optional<Receipt> receiptOptional = receiptRepository.findById(id);
        Receipt receipt = getReceipt(id, receiptOptional);
        if (cashier.getRole() == UserRole.EMPLOYEE || cashier.getRole() == UserRole.MANAGER) {
            if (!cashier.getEmployee()
                    .getPharmacy()
                    .getId()
                    .equals(receipt.getPharmacy().getId())) {
                throw new NotEmployeeException("You are not the employee of the cashier");
            }
        }
        Operation operation = new Operation();
        operation.setCashier(cashier);
        operation.setType(OperationType.RECEIPT_RETURNED);

        operation.setReceipt(receipt);
        operationRepository.save(operation);

        receipt.setStatus(ReceiptStatus.RETURNED);
        receiptRepository.save(receipt);

        for (ReceiptItem receiptItem : receipt.getReceiptItems()) {
            if (receiptItem.getStatus() != ItemStatus.SATISFIED) {
                continue;
            }

            PharmacyDrug pd = receiptItem.getPharmacyDrug();
            int unitsPerPack = pd.getDrug().getUnits();

            int totalUnitsSold = 0;
            if (receiptItem.getPack() != null) {
                totalUnitsSold += receiptItem.getPack() * unitsPerPack;
            }
            if (receiptItem.getUnits() != null) {
                totalUnitsSold += receiptItem.getUnits();
            }

            pd.setStock(pd.getStock() + totalUnitsSold);
            pharmacyDrugRepository.save(pd);
        }

        return mapReceiptDto(receipt);
    }

    @PreAuthorize("hasAnyRole('MANAGER', 'EMPLOYEE')")
    @Transactional
    public ReceiptDto updateReceiptStatus(Integer id, ReceiptStatus status, User cashier) {
        if (status == ReceiptStatus.RETURNED) {
            return returnReceipt(id, cashier);
        }
        return null;
    }

    public List<ReceiptDto> applyAllFilters(
            Integer cashierId,
            Integer drugId,
            Integer pharmacyId,
            Integer shiftId,
            ReceiptStatus status,
            Instant fromDate,
            Instant toDate,
            Pageable pageable) {
        Page<Receipt> receipts = receiptRepository.findAll(pageable);
        List<Receipt> receiptList = receipts.getContent();
        if (cashierId != null) {
            receiptList =
                    receiptList.stream()
                            .filter(r -> Objects.equals(r.getCashier().getId(), cashierId))
                            .toList();
        }

        if (pharmacyId != null) {
            receiptList =
                    receiptList.stream()
                            .filter(
                                    receipt ->
                                            !receipt.getReceiptItems().stream()
                                                    .filter(
                                                            r ->
                                                                    r.getPharmacyDrug()
                                                                            .getPharmacy()
                                                                            .getId()
                                                                            .equals(pharmacyId))
                                                    .toList()
                                                    .isEmpty())
                            .toList();
        }

        if (drugId != null) {
            receiptList =
                    receiptList.stream()
                            .filter(
                                    receipt ->
                                            !receipt.getReceiptItems().stream()
                                                    .filter(
                                                            r ->
                                                                    r.getPharmacyDrug()
                                                                            .getDrug()
                                                                            .getId()
                                                                            .equals(drugId))
                                                    .toList()
                                                    .isEmpty())
                            .toList();
        }

        if (shiftId != null) {
            receiptList =
                    receiptList.stream()
                            .filter(
                                    receipt ->
                                            receipt.getCashier()
                                                    .getEmployee()
                                                    .getShift()
                                                    .getId()
                                                    .equals(shiftId))
                            .toList();
        }

        if (status != null) {
            receiptList =
                    receiptList.stream().filter(receipt -> receipt.getStatus() == status).toList();
        }

        if (fromDate != null) {
            receiptList =
                    receiptList.stream()
                            .filter(receipt -> receipt.getCreatedAt().isAfter(fromDate))
                            .toList();
        }

        if (toDate != null) {
            receiptList =
                    receiptList.stream()
                            .filter(receipt -> receipt.getCreatedAt().isBefore(toDate))
                            .toList();
        }

        return streamAndMap(receiptList);
    }
}
