package com.fts.jpa.repository.poetry;

import com.fts.jpa.entity.poetry.Author;
import org.springframework.stereotype.Repository;

@Repository
public interface PoetryAuthorDao extends BaseDao<Author, String> {
}
