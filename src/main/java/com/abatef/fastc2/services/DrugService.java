package com.abatef.fastc2.services;

import com.abatef.fastc2.dtos.drug.DrugCreationRequest;
import com.abatef.fastc2.dtos.drug.DrugInfo;
import com.abatef.fastc2.dtos.drug.DrugUpdateRequest;
import com.abatef.fastc2.exceptions.DrugNotFoundException;
import com.abatef.fastc2.models.Drug;
import com.abatef.fastc2.models.User;
import com.abatef.fastc2.repositories.DrugRepository;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class DrugService {
    private final DrugRepository drugRepository;
    private final ModelMapper modelMapper;
    private final UserService userService;

    public DrugService(
            DrugRepository drugRepository, ModelMapper modelMapper, UserService userService) {
        this.drugRepository = drugRepository;
        this.modelMapper = modelMapper;
        this.userService = userService;
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

    public Drug getDrugByIdOrThrow(Integer id) {
        Optional<Drug> drugOptional = drugRepository.getDrugById(id);
        if (drugOptional.isPresent()) {
            return drugOptional.get();
        }
        throw new DrugNotFoundException(id);
    }

    public Optional<Drug> getDrugOptional(Integer id) {
        return drugRepository.getDrugById(id);
    }

    public Boolean existsById(Integer id) {
        return drugRepository.existsById(id);
    }

    @Transactional
    public DrugInfo updateDrugInfo(DrugUpdateRequest info, User user) {
        Drug drug = getDrugByIdOrThrow(info.getId());
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
        return modelMapper.map(drug, DrugInfo.class);
    }

    @Transactional
    public DrugInfo updateDrugPrice(Integer id, Float price, User user) {
        Drug drug = getDrugByIdOrThrow(id);
        drug.setFullPrice(price);
        drug = drugRepository.save(drug);
        return modelMapper.map(drug, DrugInfo.class);
    }

    @Transactional
    public DrugInfo updateDrugForm(Integer id, String form, User user) {
        Drug drug = getDrugByIdOrThrow(id);
        drug.setForm(form);
        drug = drugRepository.save(drug);
        return modelMapper.map(drug, DrugInfo.class);
    }

    @Transactional
    public DrugInfo updateDrugName(Integer id, String name, User user) {
        Drug drug = getDrugByIdOrThrow(id);
        drug.setName(name);
        drug = drugRepository.save(drug);
        return modelMapper.map(drug, DrugInfo.class);
    }

    @Transactional
    public DrugInfo updateDrugUnits(Integer id, Short units, User user) {
        Drug drug = getDrugByIdOrThrow(id);
        drug.setUnits(units);
        drug = drugRepository.save(drug);
        return modelMapper.map(drug, DrugInfo.class);
    }

    public void deleteDrugById(Integer id) {
        drugRepository.deleteById(id);
    }

    public List<DrugInfo> searchByName(String drugName, Pageable pageable) {
        String formattedName = drugName.trim().toLowerCase().replace(' ', '&');
        return drugRepository
                .searchDrugByNamePaginated(drugName, formattedName, pageable)
                .stream()
                .map(drug -> modelMapper.map(drug, DrugInfo.class))
                .toList();
    }

    /* Main Page
     * view -> total profit, loss, revenue
     * filter -> data, time, emp, search
     * view -> sell and by ops
     * */

    /* Expiry Page
     * view -> expiry, expired, when
     * filter -> search, near expiry, sort(expiry), quantity(asc, desc)
     * */

}
