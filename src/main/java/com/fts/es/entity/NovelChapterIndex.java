package com.fts.es.entity;

import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serial;
import java.io.Serializable;

@Data
@Document(indexName = "novel_chapter", createIndex = false)
@Builder
public class NovelChapterIndex implements Serializable {
    @Serial
    private static final long serialVersionUID = -2023545141074109418L;
    @Id
    private Long id;
    private String novelId;
    @Field(type = FieldType.Text, analyzer = "ik_smart")
    private String novelName;
    private String authorId;
    @Field(type = FieldType.Text, analyzer = "ik_smart")
    private String catalog;

    private String chapterId;
    private Integer chapterNo;
    @Field(type = FieldType.Text, analyzer = "ik_smart")
    private String content;
}
