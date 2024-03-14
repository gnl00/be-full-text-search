package com.fts.es.repository;

import com.fts.es.entity.NovelChapterIndex;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NovelChapterRepository extends ElasticsearchRepository<NovelChapterIndex, Long> {
}
