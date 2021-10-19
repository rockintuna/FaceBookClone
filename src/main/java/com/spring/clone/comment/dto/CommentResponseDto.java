package com.spring.clone.comment.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class CommentResponseDto {
    private Long commentId;
    private String content;
    private LocalDateTime createdAt;
    private String userId;
    private String userImageUrl;
    private String firstName;
    private String lastName;
}
