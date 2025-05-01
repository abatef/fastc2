package com.abatef.fastc2.repositories;

import com.abatef.fastc2.models.pharmacy.OrderStats;
import com.abatef.fastc2.models.pharmacy.OrderStatsId;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderStatsRepository extends JpaRepository<OrderStats, OrderStatsId> {
    Optional<OrderStats> getDrugOrderById(OrderStatsId id);

    Optional<OrderStats> getDrugOrderByPharmacy_IdAndDrug_Id(Integer pharmacyId, Integer drugId);
}
