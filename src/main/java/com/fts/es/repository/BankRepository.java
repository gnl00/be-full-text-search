package com.fts.es.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BankRepository extends ElasticsearchRepository<com.fts.es.entity.Bank, Long> {
}
