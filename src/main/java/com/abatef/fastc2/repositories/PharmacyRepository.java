package com.abatef.fastc2.repositories;

import com.abatef.fastc2.models.Drug;
import com.abatef.fastc2.models.pharmacy.Pharmacy;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PharmacyRepository extends JpaRepository<Pharmacy, Integer> {
    Optional<Pharmacy> getPharmacyById(Integer id);

    List<Pharmacy> getPharmaciesByOwner_Id(Integer ownerId);

    @Query(
            value =
                    "select * from pharmacies p"
                            + " where p.search_vector @@ to_tsquery('english', :tsquery)"
                            + " or p.name % :query"
                            + " order by ts_rank(p.search_vector, to_tsquery('english', :tsquery)) desc,"
                            + " similarity(p.name, :query) desc",
            countQuery =
                    "select count(*) from pharmacies p"
                            + " where p.search_vector @@ to_tsquery('english', :tsquery)"
                            + " or p.name % :query"
                            + " order by ts_rank(p.search_vector, to_tsquery('english', :tsquery)) desc,"
                            + " similarity(p.name, :query) desc",
            nativeQuery = true)
    Page<Drug> searchPharmacyByNamePaginated(
            @Param("query") String query, @Param("tsquery") String tsquery, Pageable pageable);

    Boolean existsPharmacyById(Integer id);
}
