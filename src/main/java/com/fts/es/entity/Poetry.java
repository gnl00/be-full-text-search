package com.fts.es.entity;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serial;
import java.io.Serializable;

@Document(indexName = "poetry", createIndex = true)
@Data
@Builder
public class Poetry implements Serializable {
    @Serial
    private static final long serialVersionUID = -1567692406129608119L;
    @Id
    private Long id;
    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String title;
    @Field(type = FieldType.Keyword)
    private String author;
    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String paragraphs;
    @Field(type = FieldType.Keyword)
    private String category;
}
