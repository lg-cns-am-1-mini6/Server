package com.aminspire.domain.article.domain;
//엔티티 정의. 데이터베이스와 매핑 될 클래스. JPA를 사용해 DB에 저장될 데이터를 정의

import com.aminspire.domain.common.model.BaseTimeEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;

@Getter
@Entity
public class Article extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "article_id")
    private Long id;

    private String articleName;
}