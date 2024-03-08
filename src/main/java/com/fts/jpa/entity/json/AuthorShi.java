package com.fts.jpa.entity.json;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;

/**
 * 诗作者
 */

@Entity
@Table(name = "author")
@Getter
@Setter
@ToString
public class AuthorShi implements Serializable {
    @Serial
    private static final long serialVersionUID = 4657012591606447434L;
    @Id
    private String id;
    private String name;
    private String desc;
}
