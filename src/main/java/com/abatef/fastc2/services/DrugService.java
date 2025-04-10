package com.abatef.fastc2.services;

import com.abatef.fastc2.dtos.drug.DrugCreationRequest;
import com.abatef.fastc2.dtos.drug.DrugInfo;
import com.abatef.fastc2.exceptions.DrugNotFoundException;
import com.abatef.fastc2.models.Drug;
import com.abatef.fastc2.models.User;
import com.abatef.fastc2.repositories.DrugRepository;

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
        drug.setUnits(request.getUnits());
        drug.setFullPrice(request.getPrice());
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

    @Transactional
    public Drug updateDrugInfo(DrugInfo info, User user) {
        Drug drug = getDrugById(info.getId());
        if (info.getName() != null) {
            drug.setName(info.getName());
        }
        if (info.getForm() != null) {
            drug.setForm(info.getForm());
        }

        if (info.getFullPrice() != null) {
            drug.setFullPrice(info.getFullPrice());
        }

        if (info.getUnits() != null) {
            drug.setUnits(info.getUnits());
        }

        drug = drugRepository.save(drug);
        return drug;
    }

    public void deleteDrugById(Integer id) {
        drugRepository.deleteById(id);
    }


    /* Main Page
    * view -> total profit, loss, revenue
    * filter -> data, time, emp, search
    * view -> sell and by ops
    * */

    /* Expiry Page
    * view -> expiry, expired, when
    * filter -> search, near expiry, sort(expiry), quantity(asc, desc)
    *
    * */

}
