package com.fts.jpa.entity.poetry;

import com.fts.jpa.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serial;

/**
 * 全唐诗
 */
@Entity
@Table(name = "tangshi")
@Getter
@Setter
@ToString
public class TangShi extends BasePoetry implements BaseEntity {
    @Serial
    private static final long serialVersionUID = 6104290694919985981L;
    private String title;
    private String note;
}
