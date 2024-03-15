package com.fts.jpa.dao.poetry;

import com.fts.jpa.entity.poetry.Author;
import com.fts.jpa.dao.BaseDao;
import org.springframework.stereotype.Repository;

@Repository
public interface PoetryAuthorDao extends BaseDao<Author, String> {
}
