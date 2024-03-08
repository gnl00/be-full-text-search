package com.fts.jpa.entity.json;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class YuanQuJson implements Serializable {
    @Serial
    private static final long serialVersionUID = -8518971090492161786L;
    private String title;
    private String author;
    private String dynasty;
    private String paragraphs;
}
