package com.abatef.fastc2.repositories;

import com.abatef.fastc2.enums.ReceiptStatus;
import com.abatef.fastc2.models.pharmacy.Receipt;

import jakarta.validation.constraints.NotNull;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface ReceiptRepository extends JpaRepository<Receipt, Integer> {
    Page<Receipt> findAllByCashier_Id(Integer cashierId, Pageable pageable);

    @Query(
            value =
                    "select r from Receipt r"
                            + " join ReceiptItem ri on r.id = ri.receipt.id"
                            + " where ri.pharmacyDrug.drug.id = :id",
            countQuery =
                    "select count(*) from Receipt r"
                            + " join ReceiptItem ri on r.id = ri.receipt.id"
                            + " where ri.pharmacyDrug.drug.id = :id")
    Page<Receipt> findReceiptsByDrugId(@NotNull Integer id, Pageable pageable);

    @Query(
            value =
                    "select r from Receipt r"
                            + " join ReceiptItem ri on r.id = ri.receipt.id"
                            + " where ri.pharmacyDrug.pharmacy.id = :id",
            countQuery =
                    "select count(*) from Receipt r"
                            + " join ReceiptItem ri on r.id = ri.receipt.id"
                            + " where ri.pharmacyDrug.pharmacy.id = :id")
    Page<Receipt> findReceiptsByPharmacyId(@NotNull Integer id, Pageable pageable);

    @Query(
            value = "select r from Receipt r where r.cashier.employee.shift.id = :shiftId",
            countQuery =
                    "select count(*) from Receipt r where r.cashier.employee.shift.id = :shiftId")
    Page<Receipt> findReceiptsByShiftId(@NotNull Integer shiftId, Pageable pageable);

    @Query(
            value =
                    "select DISTINCT r from Receipt r"
                            + " join ReceiptItem ri on r.id = ri.receipt.id"
                            + " join ri.pharmacyDrug pd"
                            + " where (:cashierId is null or r.cashier.id = :cashierId)"
                            + " and (:drugId is null or pd.drug.id = :drugId)"
                            + " and (:pharmacyId is null or pd.pharmacy.id = :pharmacyId)"
                            + " and (:shiftId is null or r.cashier.employee.shift.id = :shiftId)"
                            + " and (:status is null or r.status = :status)"
                            + " and (cast(:fromDate as java.time.Instant) is null or r.createdAt >= :fromDate)"
                            + " and (cast(:toDate as java.time.Instant) is null or r.createdAt <= :toDate)",
            countQuery =
                    "select count(distinct r) from Receipt r"
                            + " join ReceiptItem ri on r.id = ri.receipt.id"
                            + " join ri.pharmacyDrug pd"
                            + " where (:cashierId is null or r.cashier.id = :cashierId)"
                            + " and (:drugId is null or pd.drug.id = :drugId)"
                            + " and (:pharmacyId is null or pd.pharmacy.id = :pharmacyId)"
                            + " and (:shiftId is null or r.cashier.employee.shift.id = :shiftId)"
                            + " and (:status is null or r.status = :status)"
                            + " and (cast(:fromDate as java.time.Instant) is null or r.createdAt >= :fromDate)"
                            + " and (cast(:toDate as java.time.Instant) is null or r.createdAt <= :toDate)")
    List<Receipt> applyAllFilters(
            @Param("cashierId") Integer cashierId,
            @Param("drugId") Integer drugId,
            @Param("pharmacyId") Integer pharmacyId,
            @Param("shiftId") Integer shiftId,
            @Param("status") ReceiptStatus status,
            @Param("fromDate") Instant fromDate,
            @Param("toDate") Instant toDate,
            Pageable pageable
    );

    Receipt getReceiptById(Integer id);

    List<Receipt> findReceiptsByPharmacy_Id(Integer pharmacyId, Pageable pageable);
}
