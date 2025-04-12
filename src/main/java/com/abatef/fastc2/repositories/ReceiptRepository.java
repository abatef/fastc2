package com.abatef.fastc2.repositories;

import com.abatef.fastc2.models.Receipt;
import com.abatef.fastc2.models.pharmacy.PharmacyDrugId;

import jakarta.validation.constraints.NotNull;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface ReceiptRepository extends JpaRepository<Receipt, Integer> {
    Page<Receipt> findAllByCashier_Id(Integer cashierId, Pageable pageable);

    Page<Receipt> findAllByPharmacyDrug_Id_DrugId(
            @NotNull Integer pharmacyDrugIdDrugId, Pageable pageable);

    Page<Receipt> findAllByPharmacyDrug_Id_PharmacyId(
            @NotNull Integer pharmacyDrugIdPharmacyId, Pageable pageable);

    Page<Receipt> findAllByPharmacyDrug_Id(PharmacyDrugId pharmacyDrugId, Pageable pageable);

    Page<Receipt> findAllByShift_Id(Integer shiftId, Pageable pageable);

    @Query(
            value =
                    "select r from Receipt r " +
                            "where (:cashierId is null or :cashierId = r.cashier.id) and" +
                            " (:drugId is null or :drugId = r.pharmacyDrug.id.drugId) and" +
                            " (:pharamcyId is null or :pharmacyId = r.pharmacyDrug.id.pharmacyId) and" +
                            " (:shifId is null or :shiftId = r.shift.id) and" +
                            " (:fromDate is null or :fromDate <= r.createdAt) and" +
                            " (:toDate is null or :toDate >= r.createdAt)")
    Page<Receipt> applyAllFilters(
            Integer cashierId,
            Integer drugId,
            Integer pharmacyId,
            Integer shiftId,
            LocalDate fromDate,
            LocalDate toDate,
            Pageable pageable);
}
