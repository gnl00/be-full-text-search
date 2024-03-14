package com.fts.es.repository;

import com.fts.es.entity.NovelDetail;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NovelDetailRepository extends ElasticsearchRepository<NovelDetail, Long> {
}
