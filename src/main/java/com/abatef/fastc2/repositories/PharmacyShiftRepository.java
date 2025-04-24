package com.abatef.fastc2.repositories;

import com.abatef.fastc2.models.shift.PharmacyShift;
import com.abatef.fastc2.models.shift.PharmacyShiftId;
import com.abatef.fastc2.models.shift.Shift;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PharmacyShiftRepository extends JpaRepository<PharmacyShift, PharmacyShiftId> {
    List<Shift> getPharmacyShiftsByPharmacy_Id(Integer pharmacyId);
}
