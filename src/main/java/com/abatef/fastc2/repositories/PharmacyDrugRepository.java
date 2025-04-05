package com.abatef.fastc2.repositories;

import com.abatef.fastc2.models.PharmacyDrug;
import com.abatef.fastc2.models.PharmacyDrugId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PharmacyDrugRepository extends JpaRepository<PharmacyDrug, PharmacyDrugId> {
}
