package com.abatef.fastc2.repositories;

import com.abatef.fastc2.enums.FilterOption;
import com.abatef.fastc2.enums.SortOption;
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
                            + " join order_stats dr on dr.drug_id = pd.drug_id"
                            + " and dr.pharmacy_id = pd.pharmacy_id"
                            + " where pd.pharmacy_id = :pharmacyId and"
                            + " ((dr.required / dr.n_orders) > (select coalesce(sum(stock), 0) from pharmacy_drug pd2"
                            + " where pd2.drug_id = pd.drug_id and pd2.pharmacy_id = pd.pharmacy_id))"
                            + " order by ((dr.required / dr.n_orders) - ("
                            + " select coalesce(sum(stock), 0) from pharmacy_drug"
                            + " where pd.drug_id = drug_id and pd.pharmacy_id = pharmacy_id))",
            countQuery =
                    "select count(*) from pharmacy_drug pd"
                            + " join order_stats dr on dr.drug_id = pd.drug_id"
                            + " and dr.pharmacy_id = pd.pharmacy_id"
                            + " where pd.pharmacy_id = :pharmacyId and"
                            + " ((dr.required / dr.n_orders) > (select coalesce(sum(stock), 0)"
                            + " from pharmacy_drug where drug_id = pd.drug_id and pharmacy_id = pd.pharmacy_id))",
            nativeQuery = true)
    Page<PharmacyDrug> getPharmacyDrugsTotalWithShortage(Integer pharmacyId, Pageable pageable);

    @Query(
            value =
                    "select pd.* from pharmacy_drug pd"
                            + " join order_stats dr on dr.drug_id = pd.drug_id and dr.pharmacy_id = pd.pharmacy_id"
                            + " where pd.pharmacy_id = :pharmacyId"
                            + " and (select coalesce(sum(pd2.stock), 0) from pharmacy_drug pd2"
                            + " where pd2.drug_id = pd.drug_id and pd2.pharmacy_id = pd.pharmacy_id) = 0"
                            + " and (dr.required / dr.n_orders) != 0",
            countQuery =
                    "select count(*) from pharmacy_drug pd"
                            + " join order_stats dr on dr.drug_id = pd.drug_id and dr.pharmacy_id = pd.pharmacy_id"
                            + " where pd.pharmacy_id = :pharmacyId"
                            + " and (select coalesce(sum(pd2.stock), 0) from pharmacy_drug pd2"
                            + " where pd2.drug_id = pd.drug_id and pd2.pharmacy_id = pd.pharmacy_id) = 0"
                            + " and (dr.required / dr.n_orders) != 0",
            nativeQuery = true)
    Page<PharmacyDrug> getUnavailableShortagePharmacyDrugs(Integer pharmacyId, Pageable pageable);

    @Query(
            value =
                    "select pd.* from pharmacy_drug pd"
                            + " join order_stats dr on dr.drug_id = pd.drug_id and dr.pharmacy_id = pd.pharmacy_id"
                            + " where pd.pharmacy_id = :pharmacyId"
                            + " and (select coalesce(sum(pd2.stock), 0) from pharmacy_drug pd2"
                            + " where pd2.drug_id = pd.drug_id and pd2.pharmacy_id = pd.pharmacy_id) = 0"
                            + " and dr.required = 0",
            countQuery =
                    "select count(*) from pharmacy_drug pd"
                            + " join order_stats dr on dr.drug_id = pd.drug_id and dr.pharmacy_id = pd.pharmacy_id"
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
                    "SELECT pd.* FROM pharmacy_drug pd " +
                            "JOIN drugs d ON pd.drug_id = d.id " +
                            "WHERE pharmacy_id = :pId " +
                            "AND (" +
                            "(:tsquery != '' AND d.search_vector @@ plainto_tsquery('english', :tsquery)) OR " +
                            "(:query != '' AND (d.name % :query OR d.form % :query))" +
                            ") " +
                            "ORDER BY " +
                            "CASE WHEN :tsquery != '' AND d.search_vector @@ plainto_tsquery('english', :tsquery) " +
                            "     THEN ts_rank_cd(d.search_vector, plainto_tsquery('english', :tsquery)) ELSE 0 END DESC, " +
                            "CASE WHEN :query != '' " +
                            "     THEN greatest(similarity(d.name, :query), similarity(d.form, :query)) ELSE 0 END DESC",
            countQuery =
                    "SELECT COUNT(*) FROM pharmacy_drug pd " +
                            "JOIN drugs d ON pd.drug_id = d.id " +
                            "WHERE pharmacy_id = :pId " +
                            "AND (" +
                            "(:tsquery != '' AND d.search_vector @@ plainto_tsquery('english', :tsquery)) OR " +
                            "(:query != '' AND (d.name % :query OR d.form % :query))" +
                            ")",
            nativeQuery = true)
    Page<PharmacyDrug> searchByDrugName(
            @Param("pId") Integer pharmacyId,
            @Param("query") String query,
            @Param("tsquery") String tsquery,
            Pageable pageable);
    @Query(value = """
        SELECT pd FROM PharmacyDrug pd
        LEFT JOIN OrderStats os ON os.id.drugId = pd.drug.id AND os.id.pharmacyId = pd.pharmacy.id
        WHERE pd.pharmacy.id = :pharmacyId
        AND (:drugId IS NULL OR pd.drug.id = :drugId)
        AND (:query IS NULL OR :query = '' OR 
             LOWER(pd.drug.name) LIKE LOWER(CONCAT('%', :query, '%')) OR 
             LOWER(pd.drug.form) LIKE LOWER(CONCAT('%', :query, '%')))
        AND (
            :filterOptions IS EMPTY
            OR (
                (:availableFilter = false OR pd.stock > 0)
                AND (:shortageFilter = false OR (
                    pd.stock > 0 AND os.required IS NOT NULL AND pd.stock < os.required
                ))
                AND (:unavailableShortageFilter = false OR (
                    pd.stock = 0 AND os.required IS NOT NULL AND os.required > 0
                ))
                AND (:unavailableFilter = false OR (
                    pd.stock = 0 AND (os.required IS NULL OR os.required = 0)
                ))
                AND (:expiresAfterNFilter = false OR pd.expiryDate > :dateAfterN)
                AND (:stockOverNFilter = false OR pd.stock > :n)
                AND (:stockUnderNFilter = false OR (pd.stock < :n AND pd.stock > 0))
                AND (:outOfStockFilter = false OR pd.stock = 0)
                AND (:expiredFilter = false OR pd.expiryDate <= :today)
                AND (:approachingExpiryFilter = false OR (
                    pd.expiryDate > :today AND pd.expiryDate <= :approachingDate
                ))
                AND (:notExpiredFilter = false OR pd.expiryDate > :today)
                AND (:byFormFilter = false OR (
                    pd.drug.form IS NOT NULL AND LOWER(pd.drug.form) = LOWER(:type)
                ))
                AND (:priceBelowNFilter = false OR pd.price < :n)
                AND (:priceAboveNFilter = false OR pd.price > :n)
                AND (:priceBetweenFilter = false OR (
                    pd.price >= :n AND (:upperPriceBound IS NULL OR pd.price <= :upperPriceBound)
                ))
                AND (:discountedFilter = false OR EXISTS (
                    SELECT 1 FROM ReceiptItem ri 
                    WHERE ri.pharmacyDrug = pd AND ri.discount IS NOT NULL AND ri.discount > 0
                ))
            )
        )
        ORDER BY
            CASE WHEN :sortOption = 'EXPIRY_DATE_ASC' THEN pd.expiryDate END ASC,
            CASE WHEN :sortOption = 'EXPIRY_DATE_DESC' THEN pd.expiryDate END DESC,
            CASE WHEN :sortOption = 'PRICE_ASC' THEN pd.price END ASC,
            CASE WHEN :sortOption = 'PRICE_DESC' THEN pd.price END DESC,
            CASE WHEN :sortOption = 'STOCK_ASC' THEN pd.stock END ASC,
            CASE WHEN :sortOption = 'STOCK_DESC' THEN pd.stock END DESC
    """)
    List<PharmacyDrug> applyAllFiltersJpql(
            @Param("pharmacyId") Integer pharmacyId,
            @Param("drugId") Integer drugId,
            @Param("query") String query,
            @Param("filterOptions") List<FilterOption> filterOptions,
            @Param("sortOption") SortOption sortOption,
            @Param("n") Integer n,
            @Param("upperPriceBound") Float upperPriceBound,
            @Param("type") String type,
            @Param("today") LocalDate today,
            @Param("dateAfterN") LocalDate dateAfterN,
            @Param("approachingDate") LocalDate approachingDate,
            @Param("availableFilter") boolean availableFilter,
            @Param("shortageFilter") boolean shortageFilter,
            @Param("unavailableShortageFilter") boolean unavailableShortageFilter,
            @Param("unavailableFilter") boolean unavailableFilter,
            @Param("expiresAfterNFilter") boolean expiresAfterNFilter,
            @Param("stockOverNFilter") boolean stockOverNFilter,
            @Param("stockUnderNFilter") boolean stockUnderNFilter,
            @Param("outOfStockFilter") boolean outOfStockFilter,
            @Param("expiredFilter") boolean expiredFilter,
            @Param("approachingExpiryFilter") boolean approachingExpiryFilter,
            @Param("notExpiredFilter") boolean notExpiredFilter,
            @Param("byFormFilter") boolean byFormFilter,
            @Param("priceBelowNFilter") boolean priceBelowNFilter,
            @Param("priceAboveNFilter") boolean priceAboveNFilter,
            @Param("priceBetweenFilter") boolean priceBetweenFilter,
            @Param("discountedFilter") boolean discountedFilter,
            Pageable pageable);
    List<PharmacyDrug> getAllByPharmacy_IdAndDrug_Id(Integer pharmacyId, Integer drugId);

    List<PharmacyDrug> getAllByPharmacy_Id(Integer pharmacyId);

    List<PharmacyDrug> getAllByPharmacy_IdAndDrug_IdAndStockGreaterThan(
            Integer pharmacyId, Integer drugId, Integer stockIsGreaterThan);

    Page<PharmacyDrug> findAllById(Integer id, Pageable pageable);

    List<PharmacyDrug> getPharmacyDrugsByPharmacy_Id(Integer pharmacyId);
}
