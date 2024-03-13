package com.fts.es.entity;

import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Document;

import java.io.Serial;
import java.io.Serializable;

// TODO Index 拆分？
@Data
@Document(indexName = "novel", createIndex = false)
@Builder
public class Novel implements Serializable {
    @Serial
    private static final long serialVersionUID = -2023545141074109418L;
    @Id
    private Long id;
    private String novelId;
    private String novelName;
    private String authorId;
    private String authorName;
    private String authorIntro;
    private String authorDynasty;

    private String category; // 类别
    private String catalog; // 目录
    private String novelIntro;

    private Integer words;
    private String chapterId;
    private String content;
}
