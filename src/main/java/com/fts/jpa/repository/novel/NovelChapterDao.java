package com.fts.jpa.repository.novel;

import com.fts.jpa.entity.novel.NovelChapter;
import com.fts.jpa.repository.BaseDao;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NovelChapterDao extends BaseDao<NovelChapter, String> {
}
