package com.fts.jpa.entity.novel;

import com.fts.jpa.entity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthorNovel implements BaseEntity {
    @Serial
    private static final long serialVersionUID = 5530058416578759576L;
    private String name;
    private String intro;
    private String dynasty;
}
