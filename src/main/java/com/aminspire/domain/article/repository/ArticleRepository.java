package com.aminspire.domain.article.repository;

import com.aminspire.domain.article.domain.Article;
import java.util.List;
import java.util.Optional;

import com.aminspire.domain.user.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArticleRepository extends JpaRepository<Article, Long> {
    boolean existsByUserAndLink(User user, String link);

    List<Article> findByUser(User user);

    Optional<Article> findByIdAndUser(Long newsId, User user);
}
