package com.abatef.fastc2.services;

import com.abatef.fastc2.dtos.pharmacy.PharmacyCreationRequest;
import com.abatef.fastc2.dtos.pharmacy.PharmacyInfo;
import com.abatef.fastc2.enums.ValueType;
import com.abatef.fastc2.exceptions.NonExistingValueException;
import com.abatef.fastc2.exceptions.PharmacyDrugNotFoundException;
import com.abatef.fastc2.models.*;
import com.abatef.fastc2.repositories.PharmacyDrugRepository;
import com.abatef.fastc2.repositories.PharmacyRepository;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Optional;

@Service
public class PharmacyService {
    private final PharmacyRepository pharmacyRepository;
    private final UserService userService;
    private final DrugService drugService;
    private final ModelMapper modelMapper;
    private final PharmacyDrugRepository pharmacyDrugRepository;

    public PharmacyService(PharmacyRepository pharmacyRepository,
                           UserService userService, DrugService drugService,
                           ModelMapper modelMapper, PharmacyDrugRepository pharmacyDrugRepository) {
        this.pharmacyRepository = pharmacyRepository;
        this.userService = userService;
        this.drugService = drugService;
        this.modelMapper = modelMapper;
        this.pharmacyDrugRepository = pharmacyDrugRepository;
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
                .orElseThrow(() -> new NonExistingValueException(ValueType.ID, id.toString()));
    }

    public PharmacyInfo getPharmacyInfoById(Integer id) {
        return modelMapper.map(getPharmacyById(id), PharmacyInfo.class);
    }


    @Transactional
    public PharmacyDrug addDrugToPharmacy(PharmacyDrugId id, User user) {
        Pharmacy pharmacy = getPharmacyById(id.getPharmacyId());
        Drug drug = drugService.getDrugById(id.getDrugId());
        PharmacyDrug pharmacyDrug = new PharmacyDrug(drug, pharmacy, user);
        pharmacyDrug = pharmacyDrugRepository.save(pharmacyDrug);
        return pharmacyDrug;
    }

    private PharmacyDrug getPharmacyDrugById(PharmacyDrugId id) {
        Optional<PharmacyDrug> pdOpt = pharmacyDrugRepository.findById(id);
        if (pdOpt.isPresent()) {
            return pdOpt.get();
        }
        throw new PharmacyDrugNotFoundException(id);
    }

    @Transactional
    public PharmacyDrug updateStock(PharmacyDrugId id, Integer addedStock, User user) {
        PharmacyDrug drug = getPharmacyDrugById(id);
        drug.setStock(drug.getStock() + addedStock);
        drug = pharmacyDrugRepository.save(drug);
        return drug;
    }

    @Transactional
    public PharmacyDrug updatePrice(PharmacyDrugId id, Float newPrice, User user) {
        PharmacyDrug drug = getPharmacyDrugById(id);
        drug.setPrice(newPrice);
        drug = pharmacyDrugRepository.save(drug);
        return drug;
    }

    @Transactional
    public void deleteDrug(PharmacyDrugId id) {
        pharmacyDrugRepository.deleteById(id);
    }
}
