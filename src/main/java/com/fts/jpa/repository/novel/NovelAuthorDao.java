package com.fts.jpa.repository.novel;

import com.fts.jpa.entity.novel.AuthorNovel;
import com.fts.jpa.repository.BaseDao;
import org.springframework.stereotype.Repository;

@Repository
public interface NovelAuthorDao extends BaseDao<AuthorNovel, String> {
}
