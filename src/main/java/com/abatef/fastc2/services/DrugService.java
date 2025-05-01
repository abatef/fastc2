package com.abatef.fastc2.services;

import com.abatef.fastc2.dtos.drug.DrugCreationRequest;
import com.abatef.fastc2.dtos.drug.DrugDto;
import com.abatef.fastc2.dtos.drug.DrugUpdateRequest;
import com.abatef.fastc2.exceptions.DrugNotFoundException;
import com.abatef.fastc2.models.Drug;
import com.abatef.fastc2.models.User;
import com.abatef.fastc2.repositories.DrugRepository;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class DrugService {
    private final DrugRepository drugRepository;
    private final ModelMapper modelMapper;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public DrugService(DrugRepository drugRepository, ModelMapper modelMapper) {
        this.drugRepository = drugRepository;
        this.modelMapper = modelMapper;
    }

    @Transactional
    public Drug createNewDrug(DrugCreationRequest request, User user) {
        Drug drug = new Drug();
        drug.setName(request.getName());
        drug.setForm(request.getForm());
        drug.setUnits(request.getUnits());
        drug.setFullPrice(request.getPrice());
        drug.setCreatedBy(user);
        drug = drugRepository.save(drug);
        logger.info("new drug created by user: {}", user.getUsername());
        logger.info("drug id: {}", drug.getId());
        logger.info("drug name: {}", drug.getName());
        return drug;
    }

    public Drug getDrugByIdOrThrow(Integer id) {
        logger.info("finding drug by id: {}", id);
        Optional<Drug> drugOptional = drugRepository.findById(id);
        if (drugOptional.isPresent()) {
            logger.info("found drug with id: {}", id);
            return drugOptional.get();
        }
        logger.error("drug not found, throwing exception");
        throw new DrugNotFoundException(id);
    }

    public DrugDto getDrugInfoById(Integer id) {
        return modelMapper.map(getDrugByIdOrThrow(id), DrugDto.class);
    }

    @Transactional
    public DrugDto updateDrugInfo(DrugUpdateRequest info, User user) {
        logger.info("updating drug info: {}", info.getId());
        Drug drug = getDrugByIdOrThrow(info.getId());
        boolean isUpdated = false;
        if (info.getName() != null) {
            logger.info("updating name, old: {}, new: {}", drug.getName(), info.getName());
            drug.setName(info.getName());
            isUpdated = true;
        }
        if (info.getForm() != null) {
            logger.info("updating from, old: {}, new: {}", drug.getForm(), info.getForm());
            drug.setForm(info.getForm());
            isUpdated = true;
        }

        if (info.getFullPrice() != null) {
            logger.info(
                    "updating full price, old: {}, new: {}",
                    drug.getFullPrice(),
                    info.getFullPrice());
            drug.setFullPrice(info.getFullPrice());
            isUpdated = true;
        }

        if (info.getUnits() != null) {
            logger.info("updating units, old: {}, new: {}", drug.getUnits(), info.getUnits());
            drug.setUnits(info.getUnits());
            isUpdated = true;
        }

        if (isUpdated) {
            logger.info("saving new updates");
            drug = drugRepository.save(drug);
        } else {
            logger.info("no updates to save");
        }
        return modelMapper.map(drug, DrugDto.class);
    }

    public void deleteDrugById(Integer id) {
        drugRepository.deleteById(id);
    }

    public List<DrugDto> getAllDrugs(Pageable pageable) {
        logger.info("getting all drugs");
        return drugRepository.findAll(pageable).stream()
                .map(drug -> modelMapper.map(drug, DrugDto.class))
                .toList();
    }

    public List<DrugDto> searchByName(String drugName, Pageable pageable) {
        logger.info("searching for drug: {}", drugName);
        String formattedName = drugName.trim().toLowerCase().replace(' ', '&');
        return drugRepository.searchDrugByNamePaginated(formattedName, pageable).stream()
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
