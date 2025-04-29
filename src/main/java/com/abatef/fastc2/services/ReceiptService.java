package com.abatef.fastc2.services;

import com.abatef.fastc2.dtos.receipt.ReceiptCreationRequest;
import com.abatef.fastc2.dtos.receipt.ReceiptDto;
import com.abatef.fastc2.dtos.receipt.ReceiptItemDto;
import com.abatef.fastc2.enums.ReceiptStatus;
import com.abatef.fastc2.exceptions.InsufficientStockException;
import com.abatef.fastc2.exceptions.PharmacyDrugNotFoundException;
import com.abatef.fastc2.exceptions.ReceiptNotFoundException;
import com.abatef.fastc2.models.User;
import com.abatef.fastc2.models.pharmacy.PharmacyDrug;
import com.abatef.fastc2.models.pharmacy.Receipt;
import com.abatef.fastc2.models.pharmacy.ReceiptItem;
import com.abatef.fastc2.repositories.PharmacyDrugRepository;
import com.abatef.fastc2.repositories.ReceiptItemRepository;
import com.abatef.fastc2.repositories.ReceiptRepository;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    private final UserService userService;
    private final ModelMapper modelMapper;
    private final ShiftService shiftService;
    private final ReceiptItemRepository receiptItemRepository;
    private final PharmacyDrugRepository pharmacyDrugRepository;

    public ReceiptService(
            ReceiptRepository receiptRepository,
            PharmacyService pharmacyService,
            UserService userService,
            ModelMapper modelMapper,
            ShiftService shiftService,
            ReceiptItemRepository receiptItemRepository,
            PharmacyDrugRepository pharmacyDrugRepository) {
        this.receiptRepository = receiptRepository;
        this.pharmacyService = pharmacyService;
        this.userService = userService;
        this.modelMapper = modelMapper;
        this.shiftService = shiftService;
        this.receiptItemRepository = receiptItemRepository;
        this.pharmacyDrugRepository = pharmacyDrugRepository;
    }

    @Transactional
    public ReceiptDto createANewReceipt(List<ReceiptCreationRequest> requests, User cashier) {
        Receipt receipt = new Receipt();
        receipt.setCashier(cashier);
        receipt.setStatus(ReceiptStatus.ISSUED);
        Set<ReceiptItem> items = new HashSet<>();
        for (ReceiptCreationRequest request : requests) {
            PharmacyDrug drug =
                    pharmacyService.getNextDrugToSell(request.getPharmacyId(), request.getDrugId());
            if (drug == null) {
                throw new PharmacyDrugNotFoundException(
                        request.getPharmacyId(),
                        request.getDrugId(),
                        PharmacyDrugNotFoundException.Why.NONEXISTENT_DRUG_PHARMACY);
            }
            ReceiptItem item = new ReceiptItem();
            item.setReceipt(receipt);
            item.setPharmacyDrug(drug);
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
                    drug =
                            pharmacyService.getNextDrugToSell(
                                    request.getPharmacyId(), request.getDrugId());
                }
            }

            if (remainingQuantity > 0) {
                throw new InsufficientStockException(
                        request.getDrugId(), request.getPharmacyId(), requiredQuantity);
            }

            float amountDue = 0;
            for (Map.Entry<PharmacyDrug, Integer> entry : drugQuantities.entrySet()) {
                PharmacyDrug pd = entry.getKey();
                float pricePerUnit = pd.getPrice() / pd.getDrug().getUnits();
                amountDue += pricePerUnit * entry.getValue();
                pd.setStock(pd.getStock() - entry.getValue());
                pharmacyDrugRepository.save(pd);
            }

            item.setPack(request.getPacks());
            item.setUnits(request.getUnits());
            item.setAmountDue(amountDue);
            items.add(item);
            receipt.setReceiptItems(items);
            receipt = receiptRepository.save(receipt);
        }
        ReceiptDto info = modelMapper.map(receipt, ReceiptDto.class);
        for (ReceiptItem receiptItem : receipt.getReceiptItems()) {
            ReceiptItemDto itemInfo = new ReceiptItemDto();
            itemInfo.setDrugName(receiptItem.getPharmacyDrug().getDrug().getName());
            itemInfo.setDiscount(receiptItem.getDiscount());
            itemInfo.setPack(receiptItem.getPack());
            itemInfo.setUnits(receiptItem.getUnits());
            itemInfo.setAmountDue(receiptItem.getAmountDue());
            info.setTotal(info.getTotal() + receiptItem.getAmountDue());
            info.getItems().add(itemInfo);
        }
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

    // TODO: Complete This
    @Transactional
    public ReceiptDto updateReceiptStatus(Integer id, ReceiptStatus status, User cashier) {
        Receipt receipt = getReceiptByIdOrThrow(id);
        receipt.setStatus(status);
        receiptRepository.save(receipt);
        if (status == ReceiptStatus.RETURNED) {
            for (ReceiptItem receiptItem : receipt.getReceiptItems()) {
                PharmacyDrug pharmacyDrug = receiptItem.getPharmacyDrug();
                pharmacyDrug.setStock(pharmacyDrug.getStock() + receiptItem.getPack());
                pharmacyDrugRepository.save(pharmacyDrug);
            }
        }
        return modelMapper.map(receipt, ReceiptDto.class);
    }

    public List<ReceiptDto> getReceiptsByCashierId(Integer id, Pageable pageable) {
        Page<Receipt> receipts = receiptRepository.findAllByCashier_Id(id, pageable);
        return streamAndMap(receipts.getContent());
    }

    public List<ReceiptDto> getReceiptsByPharmacyId(Integer id, Pageable pageable) {
        Page<Receipt> receipts = receiptRepository.findReceiptsByPharmacyId(id, pageable);
        return streamAndMap(receipts.getContent());
    }

    public List<ReceiptDto> getReceiptsByDrugId(Integer id, Pageable pageable) {
        Page<Receipt> receipts = receiptRepository.findReceiptsByDrugId(id, pageable);
        return streamAndMap(receipts.getContent());
    }

    public List<ReceiptDto> getReceiptsByShiftId(Integer id, Pageable pageable) {
        Page<Receipt> receipts = receiptRepository.findReceiptsByShiftId(id, pageable);
        return streamAndMap(receipts.getContent());
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
            receiptList = receiptList.stream()
                    .filter(receipt -> receipt.getStatus() == status)
                    .toList();
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
