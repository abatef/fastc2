package com.abatef.fastc2.services;

import com.abatef.fastc2.dtos.pharmacy.SalesOperationDto;
import com.abatef.fastc2.enums.OperationStatus;
import com.abatef.fastc2.enums.OperationType;
import com.abatef.fastc2.enums.UserRole;
import com.abatef.fastc2.exceptions.NotEmployeeException;
import com.abatef.fastc2.exceptions.NotOwnerException;
import com.abatef.fastc2.models.User;
import com.abatef.fastc2.models.pharmacy.Pharmacy;
import com.abatef.fastc2.repositories.ReceiptRepository;
import com.abatef.fastc2.repositories.SalesOperationsRepository;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
}
