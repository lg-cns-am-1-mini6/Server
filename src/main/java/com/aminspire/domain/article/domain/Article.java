package com.aminspire.domain.article.domain;

import java.time.LocalDateTime;

import com.aminspire.domain.common.model.BaseTimeEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "article") // 일반적으로 테이블명은 소문자로 설정
public class Article extends BaseTimeEntity {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title")
    private String title;

    @Column(name = "originalLink")
    private String originalLink;
    
    @Column(name = "link")
    private String link;

    @Column(name = "description")
    private String description;

    @Column(name = "pub_date")
    private LocalDateTime pubDate;
}