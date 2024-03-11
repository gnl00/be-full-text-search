package com.fts.jpa.entity.novel;

import com.fts.jpa.entity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NovelChapter implements BaseEntity {
    @Serial
    private static final long serialVersionUID = 9042838287460119612L;
    private String name;
    private String catalogues;
    private Integer catalogueTotal;
    private String bookType;
    private Integer words;
    private String intro;
    private AuthorNovel author;
}