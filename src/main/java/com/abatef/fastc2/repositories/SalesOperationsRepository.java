package com.abatef.fastc2.repositories;

import com.abatef.fastc2.dtos.pharmacy.SalesOperationDto;
import com.abatef.fastc2.enums.OperationStatus;
import com.abatef.fastc2.enums.OperationType;
import com.abatef.fastc2.models.pharmacy.SalesOperation;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface SalesOperationsRepository extends JpaRepository<SalesOperation, Integer> {

    Page<SalesOperation> findAllByPharmacy_IdAndDrug_Id(
            Integer pharmacyId, Integer drugId, Pageable pageable);

    Page<SalesOperation> findAllByPharmacy_IdAndStatus(
            Integer pharmacyId, OperationStatus status, Pageable pageable);

    Page<SalesOperation> findAllByPharmacy_IdAndType(
            Integer pharmacyId, OperationType type, Pageable pageable);

    List<SalesOperation> findAllByPharmacy_Id(Integer pharmacyId, Pageable pageable);


    @Query(
            value =
                    "select so from SalesOperation so"
                            + " where (:drugId is null or so.drug.id = :drugId)"
                            + " and (:pharmacyId is null or so.pharmacy.id = :pharmacyId)"
                            + " and (:receiptId is null or so.receipt.id = :receiptId)"
                            + " and (:orderId is null or so.order.id = :orderId)"
                            + " and (:type is null or so.type = :type)"
                            + " and (:status is null or so.status = :status)"
                            + " and (:cashierId is null or so.cashier.id = :cashierId)"
                            + " and (:fromDate is null or so.createdAt >= cast(:fromDate as timestamp))"
                            + " and (:toDate is null or so.createdAt <= cast(:toDate as timestamp))",
            countQuery =
                    "select count(so) from SalesOperation so"
                            + " where (:drugId is null or so.drug.id = :drugId)"
                            + " and (:pharmacyId is null or so.pharmacy.id = :pharmacyId)"
                            + " and (:receiptId is null or so.receipt.id = :receiptId)"
                            + " and (:orderId is null or so.order.id = :orderId)"
                            + " and (:type is null or so.type = :type)"
                            + " and (:status is null or so.status = :status)"
                            + " and (:cashierId is null or so.cashier.id = :cashierId)"
                            + " and (:fromDate is null or so.createdAt >= cast(:fromDate as timestamp))"
                            + " and (:toDate is null or so.createdAt <= cast(:toDate as timestamp))")
    Page<SalesOperation> applyAllFilters(
            @Param("drugId") Integer drugId,
            @Param("pharmacyId") Integer pharmacyId,
            @Param("receiptId") Integer receiptId,
            @Param("orderId") Integer orderId,
            @Param("type") OperationType type,
            @Param("status") OperationStatus status,
            @Param("cashierId") Integer cashierId,
            @Param("fromDate") Instant fromDate,
            @Param("toDate") Instant toDate,
            Pageable pageable);

}
