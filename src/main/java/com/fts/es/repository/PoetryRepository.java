package com.fts.es.repository;

import com.fts.es.entity.Poetry;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PoetryRepository extends ElasticsearchRepository<Poetry, Long> {
}
