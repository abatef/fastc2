package com.abatef.fastc2.services;

import com.abatef.fastc2.dtos.pharmacy.PharmacyCreationRequest;
import com.abatef.fastc2.dtos.pharmacy.PharmacyInfo;
import com.abatef.fastc2.enums.ValueType;
import com.abatef.fastc2.exceptions.NonExistingValueException;
import com.abatef.fastc2.models.Pharmacy;
import com.abatef.fastc2.models.User;
import com.abatef.fastc2.repositories.PharmacyRepository;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

@Service
public class PharmacyService {
    private final PharmacyRepository pharmacyRepository;
    private final UserService userService;
    private final ModelMapper modelMapper;

    public PharmacyService(PharmacyRepository pharmacyRepository, UserService userService, ModelMapper modelMapper) {
        this.pharmacyRepository = pharmacyRepository;
        this.userService = userService;
        this.modelMapper = modelMapper;
    }

    @Transactional
    public PharmacyInfo createPharmacy(@RequestBody PharmacyCreationRequest request, @AuthenticationPrincipal User user) {
        Pharmacy pharmacy = new Pharmacy();
        pharmacy.setAddress(request.getAddress());
        pharmacy.setIsBranch(request.getIsBranch());
        pharmacy.setOwner(user);
        if (request.getIsBranch()) {
            Pharmacy mainBranch = pharmacyRepository.getPharmacyById(request.getMainBranchId());
            pharmacy.setMainBranch(mainBranch);
        }
        pharmacy.setLocation(request.getLocation().toPoint());
        return modelMapper.map(pharmacy, PharmacyInfo.class);
    }

    public Pharmacy getPharmacyById(Integer id) {
        return pharmacyRepository.findById(id)
                .orElseThrow(() -> new NonExistingValueException(ValueType.ID, "Pharmacy with id " + id + " not found"));
    }

    public PharmacyInfo getPharmacyInfoById(Integer id) {
        return modelMapper.map(getPharmacyById(id), PharmacyInfo.class);
    }

}
