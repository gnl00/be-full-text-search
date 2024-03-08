package com.fts.jpa.entity.json;

import com.fts.jpa.entity.BaseEntity;
import com.fts.jpa.entity.BasePoetry;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serial;

@Getter
@Setter
@ToString
public class SongCi300 extends BasePoetry implements BaseEntity {
    @Serial
    private static final long serialVersionUID = -1619397114209318394L;

    private String rhythmic; // 词牌名
    private String[] tags;
}
