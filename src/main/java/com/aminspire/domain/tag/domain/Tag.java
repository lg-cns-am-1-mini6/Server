package com.aminspire.domain.tag.domain;

import com.aminspire.domain.common.model.BaseTimeEntity;
import com.aminspire.domain.user.domain.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.text.Normalizer;
import java.util.Optional;

@Getter
@Entity
@NoArgsConstructor
public class Tag extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String keyword;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User searcher;

    @NotNull
    private int score;
    @Builder
    public Tag(String keyword, User searcher, int score){
        this.keyword = keyword;
        this.searcher = searcher;
        this.score = score;
    }

    public static Tag createTag(String keyword, User searcher){
        return Tag.builder()
                .keyword(keyword)
                .searcher(searcher)
                .score(1) // 로그 적용 위해 기본값 1로 설정
                .build();
    }

    public void increaseScore() {
        this.score += 1;
    }

}
