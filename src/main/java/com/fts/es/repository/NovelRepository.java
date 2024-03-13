package com.fts.es.repository;

import com.fts.es.entity.Novel;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NovelRepository extends ElasticsearchRepository<Novel, Long> {
}
