package com.aminspire.domain.article.domain;

import com.aminspire.domain.common.model.BaseTimeEntity;
import com.aminspire.domain.user.domain.user.User;
import jakarta.persistence.*;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "article") // 일반적으로 테이블명은 소문자로 설정
public class Article extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "title")
    private String title;

    @Column(name = "link")
    private String link;

    @Column(name = "description")
    private String description;

    @Column(name = "pub_date")
    private String pubDate;

    // User와 ManyToOne 관계 설정 (user_id 외래키 추가)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false) // 외래키 이름: user_id
    private User user;
}
