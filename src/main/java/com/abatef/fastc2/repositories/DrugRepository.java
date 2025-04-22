package com.abatef.fastc2.repositories;

import com.abatef.fastc2.models.Drug;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DrugRepository extends JpaRepository<Drug, Integer> {
    Optional<Drug> getDrugById(Integer id);

    @Query(
            value =
                    """
        SELECT * FROM drugs d
        WHERE plainto_tsquery('english', :query) @@ d.search_vector
           OR similarity(d.name, :query) > 0.1
           OR similarity(d.form, :query) > 0.1
        ORDER BY
            ts_rank(d.search_vector, plainto_tsquery('english', :query)) DESC,
            similarity(d.name, :query) + similarity(d.form, :query) DESC
        """,
            countQuery =
                    """
        SELECT COUNT(*) FROM drugs d
        WHERE plainto_tsquery('english', :query) @@ d.search_vector
           OR similarity(d.name, :query) > 0.1
           OR similarity(d.form, :query) > 0.1
        """,
            nativeQuery = true)
    Page<Drug> searchDrugByNamePaginated(@Param("query") String query, Pageable pageable);

    Boolean existsDrugById(Integer id);
}
