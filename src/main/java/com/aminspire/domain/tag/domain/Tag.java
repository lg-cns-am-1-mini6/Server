package com.aminspire.domain.tag.domain;

import com.aminspire.domain.common.model.BaseTimeEntity;
import com.aminspire.domain.user.domain.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

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
    private Instant lastSearchedAt;

    @NotNull
    private int searchCount;
    @Builder
    public Tag(String keyword, User searcher, int searchCount, Instant lastSearchedAt){
        this.keyword = keyword;
        this.searcher = searcher;
        this.searchCount = searchCount;
        this.lastSearchedAt = lastSearchedAt;
    }

    public static Tag createTag(String keyword, User searcher){
        return Tag.builder()
                .keyword(keyword)
                .searcher(searcher)
                .searchCount(1)
                .lastSearchedAt(Instant.now())
                // 로그 적용 위해 기본값 1로 설정
                .build();
    }

    public void increaseSearchCount() {
        this.searchCount += 1;
        this.lastSearchedAt = Instant.now();
    }

    public double calculateScore() {
        double logScore = Math.log10(searchCount + 1);  // 로그 변환

        long currentTime = Instant.now().toEpochMilli();
        long lastTime = lastSearchedAt.toEpochMilli();

        double timeDecay = 1.0 / (1 + Math.exp(-0.0001 * (currentTime - lastTime)));

        return logScore * timeDecay;  // 최종 점수 반환
    }

}
