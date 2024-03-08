package com.fts.jpa.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serial;

@Entity
@Table(name = "yuanqu")
@Getter
@Setter
@ToString
public class YuanQu extends BasePoetry implements BaseEntity {
    @Serial
    private static final long serialVersionUID = 5951772321803075051L;
    protected String title;
}
