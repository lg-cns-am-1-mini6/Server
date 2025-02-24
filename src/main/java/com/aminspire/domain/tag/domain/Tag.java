package com.aminspire.domain.tag.domain;

import com.aminspire.domain.common.model.BaseTimeEntity;
import com.aminspire.domain.user.domain.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
@Entity
public class Tag extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tag_id")
    private Long id;

    @NotBlank
    private String keyword;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "searcher_id")
    private User searcher;

    @NotNull
    private int score;
}
