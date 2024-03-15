package com.fts.jpa.dao.novel;

import com.fts.jpa.entity.novel.Novel;
import com.fts.jpa.dao.BaseDao;
import org.springframework.stereotype.Repository;

@Repository
public interface NovelDao extends BaseDao<Novel, String> {
}
