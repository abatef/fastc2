package com.abatef.fastc2.services;

import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Operator;
import com.abatef.fastc2.models.Drug;
import com.abatef.fastc2.models.DrugDocument;
import com.abatef.fastc2.repositories.DrugElasticsearchRepository;
import com.abatef.fastc2.repositories.DrugRepository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.data.elasticsearch.client.elc.Queries.matchQuery;

@Service
public class SearchService {
    private final DrugRepository drugRepository;
    private final DrugElasticsearchRepository elasticsearchRepository;
    private final ElasticsearchOperations elasticsearchOperations;
    private final ElasticsearchTemplate template;

    public SearchService(
            DrugRepository drugRepository,
            DrugElasticsearchRepository elasticsearchRepository,
            ElasticsearchOperations elasticsearchOperations, ElasticsearchTemplate template) {
        this.drugRepository = drugRepository;
        this.elasticsearchRepository = elasticsearchRepository;
        this.elasticsearchOperations = elasticsearchOperations;
        this.template = template;
    }

    public DrugDocument convertToDocument(Drug drug) {
        return new DrugDocument(
                drug.getId(),
                drug.getName(),
                drug.getForm(),
                drug.getUnits(),
                drug.getFullPrice()
        );
    }

    @PostConstruct
    public void syncDrugsToElasticsearch() {
        List<Drug> drugs = drugRepository.findAll();
        List<DrugDocument> drugDocuments = new ArrayList<>();

        for (Drug d : drugs) {
            drugDocuments.add(convertToDocument(d));
        }

        elasticsearchRepository.saveAll(drugDocuments);
    }


    public List<DrugDocument> fuzzySearchByName(String name, Pageable pageable) {
        Criteria nameCriteria = new Criteria("name").fuzzy(name);
        Query query = new CriteriaQuery(nameCriteria).setPageable(pageable);

        SearchHits<DrugDocument> hits = elasticsearchOperations.search(query, DrugDocument.class);

        return hits.stream().map(SearchHit::getContent).toList();
    }



    public List<DrugDocument> searchByPriceRange(Float minPrice, Float maxPrice) {
        return elasticsearchRepository.findByFullPriceBetween(minPrice, maxPrice);
    }
}
