package com.abatef.fastc2.services;

import com.abatef.fastc2.dtos.drug.DrugCreationRequest;
import com.abatef.fastc2.dtos.drug.DrugInfo;
import com.abatef.fastc2.exceptions.DrugNotFoundException;
import com.abatef.fastc2.models.Drug;
import com.abatef.fastc2.models.User;
import com.abatef.fastc2.repositories.DrugRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class DrugService {
    private final DrugRepository drugRepository;

    public DrugService(DrugRepository drugRepository) {
        this.drugRepository = drugRepository;
    }

    @Transactional
    public Drug createNewDrug(DrugCreationRequest request, User user) {
        Drug drug = new Drug();
        drug.setName(request.getName());
        drug.setForm(request.getForm());
        drug.setCreatedBy(user);
        return drugRepository.save(drug);
    }

    public Drug getDrugById(Integer id) {
        Optional<Drug> drugOptional = drugRepository.getDrugById(id);
        if (drugOptional.isPresent()) {
            return drugOptional.get();
        }
        throw new DrugNotFoundException(id);
    }

    @PreAuthorize("hasRole('OWNER')")
    @Transactional
    public Drug updateDrugInfo(DrugInfo info, User user) {
        Drug drug = getDrugById(info.getId());
        if (info.getName() != null) {
            drug.setName(info.getName());
        }
        if (info.getForm() != null) {
            drug.setForm(info.getForm());
        }
        drug = drugRepository.save(drug);
        return drug;
    }

    public void deleteDrugById(Integer id) {
        drugRepository.deleteById(id);
    }
}
