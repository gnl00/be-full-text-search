package com.fts.jpa.entity.novel;

import com.fts.jpa.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;

@Entity
@Table(name = "novel_chapter")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NovelChapter implements BaseEntity {
    @Serial
    private static final long serialVersionUID = 7923214661909345338L;
    @Id
    private String id;
    @Column(name = "novel_id")
    private String novelId;
    @Column(name = "novel_name")
    private String novelName;
    private String chapter;
    private String content;
}
