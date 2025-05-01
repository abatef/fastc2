package com.abatef.fastc2.repositories;

import com.abatef.fastc2.models.pharmacy.DrugOrder;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface DrugOrderRepository extends JpaRepository<DrugOrder, Integer> {

    @Query(
            value =
                    "select d from DrugOrder d where"
                            + " (:drugId is null or d.drug.id = :drugId) and"
                            + " (:phId is null or d.pharmacy.id = :phId) and"
                            + " (:userId is null or d.orderedBy.id = :userId)",
            countQuery =
                    "select count(d) from DrugOrder d where"
                            + " (:drugId is null or d.drug.id = :drugId) and"
                            + " (:phId is null or d.pharmacy.id = :phId) and"
                            + " (:userId is null or d.orderedBy.id = :userId)")
    Page<DrugOrder> findDrugOrdersFiltered(
            Integer drugId, Integer phId, Integer userId, Pageable pageable);

    DrugOrder getDrugOrderById(Integer id);

    DrugOrder getDrugOrderByIdAndPharmacy_Id(Integer id, Integer pharmacyId);
}
