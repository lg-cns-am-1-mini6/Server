package com.aminspire.domain.article.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

// News 엔티티에 대한 CRUD 연산을 처리
//  JPA의 CrudRepository나 JpaRepository를 상속받아 기본적인 CRUD 메서드를 사용
import com.aminspire.domain.article.domain.Article;

public interface ArticleRepository extends JpaRepository<Article, Long> {
	boolean existsByUserIdAndLink(Long userId, String link);

	List<Article> findByUserId(Long userId);

	Optional<Article> findByIdAndUserId(Long userId, Long id);
}