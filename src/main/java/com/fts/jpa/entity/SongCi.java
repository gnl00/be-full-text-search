package com.fts.jpa.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serial;

/**
 * 宋词
 */
@Entity
@Table(name = "songci")
@Getter
@Setter
@ToString
public class SongCi extends BasePoetry implements BaseEntity {
    @Serial
    private static final long serialVersionUID = -1619397114209318394L;
    private String rhythmic; // 词牌名
}
