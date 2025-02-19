package com.aminspire.domain.user.repository;

import com.aminspire.domain.user.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
