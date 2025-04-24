package com.abatef.fastc2.services;

import com.abatef.fastc2.dtos.receipt.ReceiptCreationRequest;
import com.abatef.fastc2.dtos.receipt.ReceiptInfo;
import com.abatef.fastc2.dtos.receipt.ReceiptItemInfo;
import com.abatef.fastc2.enums.ReceiptStatus;
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

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

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
    public ReceiptInfo createANewReceipt(List<ReceiptCreationRequest> requests, User cashier) {
        Receipt receipt = new Receipt();
        receipt.setCashier(cashier);
        receipt.setStatus(ReceiptStatus.ISSUED);
        receiptRepository.save(receipt);
        for (ReceiptCreationRequest request : requests) {
            PharmacyDrug drug =
                    pharmacyService.getPharmacyDrugByIdOrThrow(request.getPharmacyDrugId());
            ReceiptItem item = new ReceiptItem();
            item.setReceipt(receipt);
            item.setPharmacyDrug(drug);
            item.setPack(request.getPacks());
            drug.setStock(drug.getStock() - item.getPack());
            pharmacyDrugRepository.save(drug);
            item.setUnits(request.getUnits());
            item.setAmountDue(
                    (drug.getPrice() / request.getUnits())
                            + (drug.getPrice() * request.getUnits()));
            item = receiptItemRepository.save(item);
            receipt.getReceiptItems().add(item);
        }
        ReceiptInfo info = modelMapper.map(receipt, ReceiptInfo.class);
        for (ReceiptItem receiptItem : receipt.getReceiptItems()) {
            ReceiptItemInfo itemInfo = new ReceiptItemInfo();
            itemInfo.setDrugName(receiptItem.getPharmacyDrug().getDrug().getName());
            itemInfo.setDiscount(receiptItem.getDiscount());
            itemInfo.setPack(receiptItem.getPack());
            itemInfo.setUnits(receiptItem.getUnits());
            itemInfo.setAmountDue(receiptItem.getAmountDue());
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

    public ReceiptInfo getReceiptInfoById(Integer id) {
        return modelMapper.map(getReceiptByIdOrThrow(id), ReceiptInfo.class);
    }

    private List<ReceiptInfo> streamAndMap(List<Receipt> receipts) {
        return receipts.stream()
                .map(receipt -> modelMapper.map(receipt, ReceiptInfo.class))
                .toList();
    }

    // TODO: Complete This
    @Transactional
    public ReceiptInfo updateReceiptStatus(Integer id, ReceiptStatus status, User cashier) {
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
        return modelMapper.map(receipt, ReceiptInfo.class);
    }

    public List<ReceiptInfo> getReceiptsByCashierId(Integer id, Pageable pageable) {
        Page<Receipt> receipts = receiptRepository.findAllByCashier_Id(id, pageable);
        return streamAndMap(receipts.getContent());
    }

    public List<ReceiptInfo> getReceiptsByPharmacyId(Integer id, Pageable pageable) {
        Page<Receipt> receipts = receiptRepository.findReceiptsByPharmacyId(id, pageable);
        return streamAndMap(receipts.getContent());
    }

    public List<ReceiptInfo> getReceiptsByDrugId(Integer id, Pageable pageable) {
        Page<Receipt> receipts = receiptRepository.findReceiptsByDrugId(id, pageable);
        return streamAndMap(receipts.getContent());
    }

    public List<ReceiptInfo> getReceiptsByShiftId(Integer id, Pageable pageable) {
        Page<Receipt> receipts = receiptRepository.findReceiptsByShiftId(id, pageable);
        return streamAndMap(receipts.getContent());
    }

    public List<ReceiptInfo> applyAllFilters(
            Integer cashierId,
            Integer drugId,
            Integer pharmacyId,
            Integer shiftId,
            LocalDate fromDate,
            LocalDate toDate,
            Pageable pageable) {
        Page<Receipt> receipts =
                receiptRepository.applyAllFilters(
                        cashierId, drugId, pharmacyId, shiftId, fromDate, toDate, pageable);
        return streamAndMap(receipts.getContent());
    }
}
