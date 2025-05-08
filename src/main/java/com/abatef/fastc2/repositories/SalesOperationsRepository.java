package com.abatef.fastc2.repositories;

import com.abatef.fastc2.dtos.pharmacy.SalesOperationDto;
import com.abatef.fastc2.enums.OperationStatus;
import com.abatef.fastc2.enums.OperationType;
import com.abatef.fastc2.models.pharmacy.SalesOperation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SalesOperationsRepository extends JpaRepository<SalesOperation, Integer> {

    Page<SalesOperation> findAllByPharmacy_IdAndDrug_Id(Integer pharmacyId, Integer drugId, Pageable pageable);

    Page<SalesOperation> findAllByPharmacy_IdAndStatus(Integer pharmacyId, OperationStatus status, Pageable pageable);

    Page<SalesOperation> findAllByPharmacy_IdAndType(Integer pharmacyId, OperationType type, Pageable pageable);

    List<SalesOperationDto> findAllByPharmacy_Id(Integer pharmacyId, Pageable pageable);
}
