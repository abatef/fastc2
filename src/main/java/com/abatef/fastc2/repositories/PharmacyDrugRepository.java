package com.abatef.fastc2.repositories;

import com.abatef.fastc2.models.pharmacy.PharmacyDrug;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PharmacyDrugRepository extends JpaRepository<PharmacyDrug, Integer> {
    Boolean existsPharmacyDrugByPharmacy_IdAndDrug_Id(Integer pharmacyId, Integer drugId);

    Page<PharmacyDrug> getPharmacyDrugsByPharmacy_Id(Integer pharmacyId, Pageable pageable);

    Page<PharmacyDrug> getPharmacyDrugsByPharmacy_IdAndDrug_Id(
            Integer pharmacyId, Integer drugId, Pageable pageable);

    Page<PharmacyDrug> getPharmacyDrugsByExpiryDateBefore(
            LocalDate expiryDateBefore, Pageable pageable);

    Page<PharmacyDrug> getPharmacyDrugsByExpiryDateAfter(
            LocalDate expiryDateAfter, Pageable pageable);

    Page<PharmacyDrug> getPharmacyDrugsByPharmacy_IdAndExpiryDateAfter(
            Integer pharmacyId, LocalDate expiryDateAfter, Pageable pageable);

    Page<PharmacyDrug> getPharmacyDrugsByPharmacy_IdAndExpiryDateBefore(
            Integer pharmacyId, LocalDate expiryDateBefore, Pageable pageable);

    Page<PharmacyDrug> getPharmacyDrugsByPharmacy_IdAndStockIsGreaterThanEqual(
            Integer pharmacyId, Integer stockIsGreaterThan, Pageable pageable);

    Page<PharmacyDrug> getPharmacyDrugsByPharmacy_IdAndStockIsLessThanEqual(
            Integer pharmacyId, Integer stockIsLessThan, Pageable pageable);

    @Query(
            value =
                    "select pd.* from pharmacy_drug pd "
                            + " join drugs d on pd.drug_id = d.id"
                            + " where pharmacy_id = :pId"
                            + " and (d.search_vector @@ to_tsquery('english', :tsquery)"
                            + " or d.name % :query or d.form % :query)"
                            + " order by ts_rank(d.search_vector, to_tsquery('english', :tsquery)) desc,"
                            + " similarity(d.name, :query) + similarity(d.form, :query)",
            countQuery =
                    "select count(*) from pharmacy_drug pd"
                            + " join drugs d on pd.drug_id = d.id"
                            + " where pharmacy_id = :pId"
                            + " and (d.search_vector @@ to_tsquery('english', :tsquery)"
                            + " or d.name % :query or d.form % :query)"
                            + " order by ts_rank(d.search_vector, to_tsquery('english', :tsquery)) desc,"
                            + " similarity(d.name, :query) + similarity(d.form, :query)",
            nativeQuery = true)
    Page<PharmacyDrug> searchByDrugName(
            @Param("pId") Integer pharmacyId,
            @Param("query") String query,
            @Param("tsquery") String tsquery,
            Pageable pageable);
}
