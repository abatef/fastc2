package com.abatef.fastc2.services;

import com.abatef.fastc2.dtos.drug.DrugCreationRequest;
import com.abatef.fastc2.dtos.drug.DrugDto;
import com.abatef.fastc2.dtos.drug.DrugUpdateRequest;
import com.abatef.fastc2.exceptions.DrugNotFoundException;
import com.abatef.fastc2.models.Drug;
import com.abatef.fastc2.models.User;
import com.abatef.fastc2.repositories.DrugRepository;

import org.modelmapper.ModelMapper;
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
        Optional<Drug> drugOptional = drugRepository.findById(id);
        if (drugOptional.isPresent()) {
            return drugOptional.get();
        }
        throw new DrugNotFoundException(id);
    }

    public DrugDto getDrugInfoById(Integer id) {
        return modelMapper.map(getDrugByIdOrThrow(id), DrugDto.class);
    }

    public Optional<Drug> getDrugOptional(Integer id) {
        return drugRepository.getDrugById(id);
    }

    public Boolean existsById(Integer id) {
        return drugRepository.existsById(id);
    }

    @Transactional
    public DrugDto updateDrugInfo(DrugUpdateRequest info, User user) {
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
        return modelMapper.map(drug, DrugDto.class);
    }

    @Transactional
    public DrugDto updateDrugPrice(Integer id, Float price, User user) {
        Drug drug = getDrugByIdOrThrow(id);
        drug.setFullPrice(price);
        drug = drugRepository.save(drug);
        return modelMapper.map(drug, DrugDto.class);
    }

    @Transactional
    public DrugDto updateDrugForm(Integer id, String form, User user) {
        Drug drug = getDrugByIdOrThrow(id);
        drug.setForm(form);
        drug = drugRepository.save(drug);
        return modelMapper.map(drug, DrugDto.class);
    }

    @Transactional
    public DrugDto updateDrugName(Integer id, String name, User user) {
        Drug drug = getDrugByIdOrThrow(id);
        drug.setName(name);
        drug = drugRepository.save(drug);
        return modelMapper.map(drug, DrugDto.class);
    }

    @Transactional
    public DrugDto updateDrugUnits(Integer id, Short units, User user) {
        Drug drug = getDrugByIdOrThrow(id);
        drug.setUnits(units);
        drug = drugRepository.save(drug);
        return modelMapper.map(drug, DrugDto.class);
    }

    public void deleteDrugById(Integer id) {
        drugRepository.deleteById(id);
    }

    public List<DrugDto> getAllDrugs(Pageable pageable) {
        return drugRepository
                .findAll(pageable)
                .stream()
                .map(drug -> modelMapper.map(drug, DrugDto.class))
                .toList();
    }

    public List<DrugDto> searchByName(String drugName, Pageable pageable) {
        String formattedName = drugName.trim().toLowerCase().replace(' ', '&');
        return drugRepository
                .searchDrugByNamePaginated(formattedName, pageable)
                .stream()
                .map(drug -> modelMapper.map(drug, DrugDto.class))
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
