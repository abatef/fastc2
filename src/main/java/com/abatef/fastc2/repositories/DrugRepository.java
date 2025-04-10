package com.abatef.fastc2.repositories;

import com.abatef.fastc2.models.Drug;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DrugRepository extends JpaRepository<Drug, Integer> {
    Optional<Drug> getDrugById(Integer id);
}
