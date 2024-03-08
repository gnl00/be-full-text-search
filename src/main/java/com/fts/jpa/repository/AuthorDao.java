package com.fts.jpa.repository;

import com.fts.jpa.entity.Author;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorDao extends BaseDao<Author, String> {
}
