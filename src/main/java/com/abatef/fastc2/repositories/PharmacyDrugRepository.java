package com.abatef.fastc2.repositories;

import com.abatef.fastc2.models.pharmacy.PharmacyDrug;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

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
                    "select pd from PharmacyDrug pd"
                            + " join DrugOrder dr on dr.drug.id = pd.drug.id"
                            + " and dr.pharmacy.id = pd.pharmacy.id"
                            + " where pd.pharmacy.id = :pharmacyId and sum(pd.stock) < (dr.required / dr.nOrders)")
    Page<PharmacyDrug> getPharmacyDrugsWithShortage(Integer pharmacyId, Pageable pageable);

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
                    "select pd from PharmacyDrug pd"
                            + " join DrugOrder dr on dr.drug.id = pd.drug.id"
                            + " and dr.pharmacy.id = pd.pharmacy.id"
                            + " where pd.pharmacy.id = :pharmacyId and sum(pd.stock) = 0 and (dr.required / dr.nOrders) != 0")
    Page<PharmacyDrug> getUnavailableShortagePharmacyDrugs(Integer pharmacyId, Pageable pageable);

    @Query(
            value =
                    "select pd from PharmacyDrug pd"
                            + " join DrugOrder dr on dr.drug.id = pd.drug.id"
                            + " and dr.pharmacy.id = pd.pharmacy.id"
                            + " where pd.pharmacy.id = :pharmacyId and sum(pd.stock) = 0 and dr.required = 0")
    Page<PharmacyDrug> getUnavailablePharmacyDrugs(Integer pharmacyId, Pageable pageable);

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
