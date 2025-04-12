package com.abatef.fastc2.services;

import com.abatef.fastc2.dtos.receipt.ReceiptCreationRequest;
import com.abatef.fastc2.dtos.receipt.ReceiptInfo;
import com.abatef.fastc2.exceptions.ReceiptNotFoundException;
import com.abatef.fastc2.models.Receipt;
import com.abatef.fastc2.models.User;
import com.abatef.fastc2.models.pharmacy.PharmacyDrug;
import com.abatef.fastc2.models.pharmacy.PharmacyDrugId;
import com.abatef.fastc2.models.shift.Shift;
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

    public ReceiptService(
            ReceiptRepository receiptRepository,
            PharmacyService pharmacyService,
            UserService userService,
            ModelMapper modelMapper,
            ShiftService shiftService) {
        this.receiptRepository = receiptRepository;
        this.pharmacyService = pharmacyService;
        this.userService = userService;
        this.modelMapper = modelMapper;
        this.shiftService = shiftService;
    }

    @Transactional
    public ReceiptInfo createANewReceipt(ReceiptCreationRequest request, User cashier) {
        Receipt receipt = new Receipt();
        PharmacyDrugId id =
                new PharmacyDrugId(
                        request.getDrugId(), request.getPharmacyId(), request.getDrugExpiryDate());
        PharmacyDrug drug = pharmacyService.getPharmacyDrugByIdOrThrow(id);
        receipt.setPharmacyDrug(drug);
        receipt.setCashier(cashier);
        receipt.setDiscount(request.getDiscount());
        receipt.setAmountDue(request.getAmountDue());
        receipt.setPacks(request.getPacks());
        receipt.setUnits(request.getUnits());
        Shift shift = shiftService.getById(request.getShiftId());
        receipt.setShift(shift);
        receipt = receiptRepository.save(receipt);
        return modelMapper.map(receipt, ReceiptInfo.class);
    }

    public Receipt getReceiptById(Integer id) {
        Optional<Receipt> receipt = receiptRepository.findById(id);
        if (receipt.isPresent()) {
            return receipt.get();
        }
        throw new ReceiptNotFoundException(id);
    }

    public ReceiptInfo getReceiptInfoById(Integer id) {
        return modelMapper.map(getReceiptById(id), ReceiptInfo.class);
    }

    private List<ReceiptInfo> streamAndMap(List<Receipt> receipts) {
        return receipts.stream()
                .map(receipt -> modelMapper.map(receipt, ReceiptInfo.class))
                .toList();
    }

    public List<ReceiptInfo> getReceiptsByCashierId(Integer id, Pageable pageable) {
        Page<Receipt> receipts = receiptRepository.findAllByCashier_Id(id, pageable);
        return streamAndMap(receipts.getContent());
    }

    public List<ReceiptInfo> getReceiptsByPharmacyId(Integer id, Pageable pageable) {
        Page<Receipt> receipts =
                receiptRepository.findAllByPharmacyDrug_Id_PharmacyId(id, pageable);
        return streamAndMap(receipts.getContent());
    }

    public List<ReceiptInfo> getReceiptsByDrugId(Integer id, Pageable pageable) {
        Page<Receipt> receipts = receiptRepository.findAllByPharmacyDrug_Id_DrugId(id, pageable);
        return streamAndMap(receipts.getContent());
    }

    public List<ReceiptInfo> getReceiptsByShiftId(Integer id, Pageable pageable) {
        Page<Receipt> receipts = receiptRepository.findAllByShift_Id(id, pageable);
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
