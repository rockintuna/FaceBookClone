package com.spring.clone.post;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikeInfoRepository extends JpaRepository<LikeInfo, Long> {
    Optional<LikeInfo> findByPostIdAndUserId(Long post_id, Long user_id);
}
