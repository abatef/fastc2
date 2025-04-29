package com.abatef.fastc2.repositories;

import com.abatef.fastc2.models.pharmacy.Receipt;

import jakarta.validation.constraints.NotNull;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.LocalDate;

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
                    "select r from Receipt r"
                            + " join ReceiptItem ri on r.id = ri.receipt.id"
                            + " where (:cashierId is null or r.cashier.id = :cashierId)"
                            + " and (:drugId is null or ri.pharmacyDrug.drug.id = :drugId)"
                            + " and (:pharmacyId is null or ri.pharmacyDrug.pharmacy.id = :pharmacyId)"
                            + " and (:shiftId is null or r.cashier.employee.shift.id = :shiftId)"
                            + " and (:fromDate is null or r.createdAt >= cast(:fromDate as timestamp))"
                            + " and (:toDate is null or r.createdAt <= cast(:toDate as timestamp))",
            countQuery =
                    "select count(r) from Receipt r"
                            + " join ReceiptItem ri on r.id = ri.receipt.id"
                            + " where (:cashierId is null or r.cashier.id = :cashierId)"
                            + " and (:drugId is null or ri.pharmacyDrug.drug.id = :drugId)"
                            + " and (:pharmacyId is null or ri.pharmacyDrug.pharmacy.id = :pharmacyId)"
                            + " and (:shiftId is null or r.cashier.employee.shift.id = :shiftId)"
                            + " and (:fromDate is null or r.createdAt >= cast(:fromDate as timestamp))"
                            + " and (:toDate is null or r.createdAt <= cast(:toDate as timestamp))")
    Page<Receipt> applyAllFilters(
            @Param("cashierId") Integer cashierId,
            @Param("drugId") Integer drugId,
            @Param("pharmacyId") Integer pharmacyId,
            @Param("shiftId") Integer shiftId,
            @Param("fromDate") Instant fromDate,
            @Param("toDate") Instant toDate,
            Pageable pageable);

}
