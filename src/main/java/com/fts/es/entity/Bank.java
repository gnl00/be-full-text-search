package com.fts.es.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.io.Serial;
import java.io.Serializable;

@Data
@Document(indexName = "bank")
public class Bank implements Serializable {
    @Serial
    private static final long serialVersionUID = 7414184214231942350L;
    @Id
    private Long id;
    private Integer accountNumber;
    private String address;
    private Integer age;
    private String gender;
    private Double balance;
    private String state;
    private String city;
    private String email;
    private String employer;
    private String firstname;
    private String lastname;
}
