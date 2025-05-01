package com.abatef.fastc2.repositories;

import com.abatef.fastc2.models.pharmacy.Operation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OperationRepository extends JpaRepository<Operation, Integer> {}
