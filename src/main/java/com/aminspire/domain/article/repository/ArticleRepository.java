package com.aminspire.domain.article.repository;
// News 엔티티에 대한 CRUD 연산을 처리
//  JPA의 CrudRepository나 JpaRepository를 상속받아 기본적인 CRUD 메서드를 사용
import com.aminspire.domain.article.domain.Article;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ArticleRepository extends JpaRepository<Article, String> {
    //List<Article> findByTitleContaining(String keyword); // 제목 검색 기능
}