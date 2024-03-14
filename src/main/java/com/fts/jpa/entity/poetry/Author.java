package com.fts.jpa.entity.poetry;

import com.fts.jpa.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.io.Serial;
import java.util.Objects;

@Entity
@Table(name = "author")
@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Author implements BaseEntity {
    @Serial
    private static final long serialVersionUID = -3419390614318681948L;
    @Id
    private String id;
    private String name;
    private String description;
    private String dynasty;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Author author = (Author) o;
        return Objects.equals(name, author.name) && Objects.equals(dynasty, author.dynasty);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, dynasty);
    }
}
