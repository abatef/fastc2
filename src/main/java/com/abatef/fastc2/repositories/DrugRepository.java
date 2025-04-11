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
                    "select * from drugs d"
                            + " where d.search_vector @@ to_tsquery('english', :tsquery)"
                            + " or d.name % :query or d.form % :query"
                            + " order by ts_rank(d.search_vector, to_tsquery('english', :tsquery)) desc,"
                            + " similarity(d.name, :query) + similarity(d.form, :query) desc",
            countQuery =
                    "select count(*) from drugs d"
                            + " where d.search_vector @@ to_tsquery('english', :tsquery)"
                            + " or d.name % :query or d.form % :query"
                            + " order by ts_rank(d.search_vector, to_tsquery('english', :tsquery)) desc,"
                            + " similarity(d.name, :query) + similarity(d.form, :query) desc",
            nativeQuery = true)
    Page<Drug> searchDrugByNamePaginated(
            @Param("query") String query, @Param("tsquery") String tsquery, Pageable pageable);

    Boolean existsDrugById(Integer id);
}
