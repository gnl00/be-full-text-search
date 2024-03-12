package com.fts.jpa.entity.novel;

import com.fts.jpa.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Entity
@Table(name = "author")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthorNovel implements BaseEntity {
    @Serial
    private static final long serialVersionUID = 5530058416578759576L;
    @Id
    private String id;
    private String name;
    private String intro;
    private String dynasty;
}
