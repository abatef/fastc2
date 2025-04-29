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
                    "select pd.* from pharmacy_drug pd"
                            + " join drug_order dr on dr.drug_id = pd.drug_id"
                            + " and dr.pharmacy_id = pd.pharmacy_id"
                            + " where pd.pharmacy_id = :pharmacyId and"
                            + " ((dr.required / dr.n_orders) > (select coalesce(sum(stock), 0) from pharmacy_drug pd2"
                            + " where pd2.drug_id = pd.drug_id and pd2.pharmacy_id = pd.pharmacy_id))"
                            + " order by ((dr.required / dr.n_orders) - ("
                            + " select coalesce(sum(stock), 0) from pharmacy_drug"
                            + " where pd.drug_id = drug_id and pd.pharmacy_id = pharmacy_id))",
            countQuery =
                    "select count(*) from pharmacy_drug pd"
                            + " join drug_order dr on dr.drug_id = pd.drug_id"
                            + " and dr.pharmacy_id = pd.pharmacy_id"
                            + " where pd.pharmacy_id = :pharmacyId and"
                            + " ((dr.required / dr.n_orders) > (select coalesce(sum(stock), 0)"
                            + " from pharmacy_drug where drug_id = pd.drug_id and pharmacy_id = pd.pharmacy_id))",
            nativeQuery = true)
    Page<PharmacyDrug> getPharmacyDrugsTotalWithShortage(Integer pharmacyId, Pageable pageable);

    @Query(
            value =
                    "select pd.* from pharmacy_drug pd"
                            + " join drug_order dr on dr.drug_id = pd.drug_id and dr.pharmacy_id = pd.pharmacy_id"
                            + " where pd.pharmacy_id = :pharmacyId"
                            + " and (select coalesce(sum(pd2.stock), 0) from pharmacy_drug pd2"
                            + " where pd2.drug_id = pd.drug_id and pd2.pharmacy_id = pd.pharmacy_id) = 0"
                            + " and (dr.required / dr.n_orders) != 0",
            countQuery =
                    "select count(*) from pharmacy_drug pd"
                            + " join drug_order dr on dr.drug_id = pd.drug_id and dr.pharmacy_id = pd.pharmacy_id"
                            + " where pd.pharmacy_id = :pharmacyId"
                            + " and (select coalesce(sum(pd2.stock), 0) from pharmacy_drug pd2"
                            + " where pd2.drug_id = pd.drug_id and pd2.pharmacy_id = pd.pharmacy_id) = 0"
                            + " and (dr.required / dr.n_orders) != 0",
            nativeQuery = true)
    Page<PharmacyDrug> getUnavailableShortagePharmacyDrugs(Integer pharmacyId, Pageable pageable);

    @Query(
            value =
                    "select pd.* from pharmacy_drug pd"
                            + " join drug_order dr on dr.drug_id = pd.drug_id and dr.pharmacy_id = pd.pharmacy_id"
                            + " where pd.pharmacy_id = :pharmacyId"
                            + " and (select coalesce(sum(pd2.stock), 0) from pharmacy_drug pd2"
                            + " where pd2.drug_id = pd.drug_id and pd2.pharmacy_id = pd.pharmacy_id) = 0"
                            + " and dr.required = 0",
            countQuery =
                    "select count(*) from pharmacy_drug pd"
                            + " join drug_order dr on dr.drug_id = pd.drug_id and dr.pharmacy_id = pd.pharmacy_id"
                            + " where pd.pharmacy_id = :pharmacyId"
                            + " and (select coalesce(sum(pd2.stock), 0) from pharmacy_drug pd2"
                            + " where pd2.drug_id = pd.drug_id and pd2.pharmacy_id = pd.pharmacy_id) = 0"
                            + " and dr.required = 0",
            nativeQuery = true)
    Page<PharmacyDrug> getUnavailablePharmacyDrugs(Integer pharmacyId, Pageable pageable);

    //    @Query(
    //            value =
    //                    "SELECT new com.abatef.fastc2.dtos.drug.PharmacyDrugShortage("
    //                            + "pd.drug, pd.pharmacy,"
    //                            + "COALESCE(dr.required - COALESCE(SUM(pd.stock), 0), 0) ) "
    //                            + "FROM PharmacyDrug pd "
    //                            + "JOIN DrugOrder dr ON pd.drug.id = dr.drug.id "
    //                            + "AND pd.pharmacy.id = dr.pharmacy.id "
    //                            + "WHERE pd.pharmacy.id = :pharmacyId "
    //                            + "GROUP BY pd.drug.id, pd.pharmacy.id, dr.required  "
    //                            + "HAVING COALESCE(SUM(pd.stock), 0) < dr.required",
    //            countQuery =
    //                    "SELECT COUNT(DISTINCT pd) "
    //                            + "FROM PharmacyDrug pd "
    //                            + "JOIN DrugOrder dr ON pd.drug.id = dr.drug.id "
    //                            + "AND pd.pharmacy.id = dr.pharmacy.id "
    //                            + "WHERE pd.pharmacy.id = :pharmacyId "
    //                            + "HAVING COALESCE(SUM(pd.stock), 0) < dr.required",
    //            nativeQuery = false)
    //    Page<PharmacyDrugShortage> getShortageDrugsByPharmacyId(Integer pharmacyId, Pageable
    // pageable);

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

    List<PharmacyDrug> getAllByPharmacy_IdAndDrug_Id(Integer pharmacyId, Integer drugId);

    List<PharmacyDrug> getAllByPharmacy_Id(Integer pharmacyId);

    List<PharmacyDrug> getAllByPharmacy_IdAndDrug_IdAndStockGreaterThan(
            Integer pharmacyId, Integer drugId, Integer stockIsGreaterThan);
}
