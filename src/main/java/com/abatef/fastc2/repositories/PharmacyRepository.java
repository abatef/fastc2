package com.abatef.fastc2.repositories;

import com.abatef.fastc2.models.Pharmacy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PharmacyRepository extends JpaRepository<Pharmacy, Integer> {
    Pharmacy getPharmacyById(Integer id);
}
