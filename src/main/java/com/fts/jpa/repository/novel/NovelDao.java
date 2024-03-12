package com.fts.jpa.repository.novel;

import com.fts.jpa.entity.novel.Novel;
import com.fts.jpa.repository.BaseDao;
import org.springframework.stereotype.Repository;

@Repository
public interface NovelDao extends BaseDao<Novel, String> {
}
