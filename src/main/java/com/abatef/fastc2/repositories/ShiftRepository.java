package com.abatef.fastc2.repositories;

import com.abatef.fastc2.models.shift.Shift;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShiftRepository extends JpaRepository<Shift, Integer> {}
