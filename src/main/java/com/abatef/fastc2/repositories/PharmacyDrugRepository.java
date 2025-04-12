package com.abatef.fastc2.repositories;

import com.abatef.fastc2.models.pharmacy.PharmacyDrug;
import com.abatef.fastc2.models.pharmacy.PharmacyDrugId;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PharmacyDrugRepository extends JpaRepository<PharmacyDrug, PharmacyDrugId> {
    Boolean existsPharmacyDrugByPharmacy_IdAndDrug_Id(Integer pharmacyId, Integer drugId);
}
