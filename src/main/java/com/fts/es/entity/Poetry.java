package com.fts.es.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serial;
import java.io.Serializable;

@Document(indexName = "poetry", createIndex = true)
@Data
public class Poetry implements Serializable {
    @Serial
    private static final long serialVersionUID = -1567692406129608119L;
    @Id
    private Long id;
    @Field(type = FieldType.Text)
    private String title;
    @Field(type = FieldType.Text)
    private String author;
    @Field(type = FieldType.Text)
    private String paragraphs;
}
