package com.fts.jpa.dao.novel;

import com.fts.jpa.entity.novel.NovelChapter;
import com.fts.jpa.dao.BaseDao;
import org.springframework.stereotype.Repository;

@Repository
public interface NovelChapterDao extends BaseDao<NovelChapter, String> {
}
