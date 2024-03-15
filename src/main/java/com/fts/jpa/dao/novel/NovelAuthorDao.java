package com.fts.jpa.dao.novel;

import com.fts.jpa.entity.novel.AuthorNovel;
import com.fts.jpa.dao.BaseDao;
import org.springframework.stereotype.Repository;

@Repository
public interface NovelAuthorDao extends BaseDao<AuthorNovel, String> {
}
