package com.aminspire.domain.tag.repository;

import com.aminspire.domain.tag.domain.Tag;
import com.aminspire.domain.user.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface TagRepository extends JpaRepository<Tag, Long> {
    List<Tag> findAllBySearcher(User searcher);

    Optional<Tag>findByKeywordAndSearcher(String keyword, User searcher);
}