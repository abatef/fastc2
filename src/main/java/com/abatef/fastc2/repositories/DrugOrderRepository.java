package com.abatef.fastc2.repositories;

import com.abatef.fastc2.models.pharmacy.DrugOrder;
import com.abatef.fastc2.models.pharmacy.DrugOrderId;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DrugOrderRepository extends JpaRepository<DrugOrder, DrugOrderId> {
    Optional<DrugOrder> getDrugOrderById(DrugOrderId id);

    Optional<DrugOrder> getDrugOrderByPharmacy_IdAndDrug_Id(Integer pharmacyId, Integer drugId);
}
