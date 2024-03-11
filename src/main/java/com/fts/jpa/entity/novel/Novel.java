package com.fts.jpa.entity.novel;

import com.fts.jpa.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.io.Serial;

@Entity
@Table(name = "novel")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Novel implements BaseEntity {
    @Serial
    private static final long serialVersionUID = 3273188676672744617L;
    @Id
    private String id;
    private String name;
    private String author;
    private String intro;
    private String catalog;
    private String category;
    private Integer words;
}
