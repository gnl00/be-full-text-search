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
@Document(indexName = "novel_detail", createIndex = false)
@Builder
public class NovelDetail implements Serializable {
    @Serial
    private static final long serialVersionUID = -2023545141074109418L;
    @Id
    private Long id;
    private String novelId;
    @Field(type = FieldType.Text, analyzer = "ik_smart")
    private String novelName;
    private String authorId;
    private String authorName;
    @Field(type = FieldType.Text, analyzer = "ik_smart")
    private String authorIntro;
    private String authorDynasty;
    @Field(type = FieldType.Text, analyzer = "ik_smart")
    private String novelIntro;
    @Field(type = FieldType.Text, analyzer = "ik_smart")
    private String category; // 类别
    private String catalog; // 目录

    private Integer words;
}
