package com.abatef.fastc2.repositories;

import com.abatef.fastc2.models.DrugDocument;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface DrugElasticsearchRepository extends ElasticsearchRepository<DrugDocument, Integer> {
    // Add custom fuzzy query method
    @Query("""
    {
      "match": {
        "name": {
          "query": "?0",
          "fuzziness": "AUTO"
        }
      }
    }
    """)
    List<DrugDocument> findByNameFuzzy(String name, Pageable pageable);

    // Original methods
    List<DrugDocument> findByNameContaining(String name);
    List<DrugDocument> findByFormContaining(String form);
    List<DrugDocument> findByFullPriceBetween(Float minPrice, Float maxPrice);
}