package com.spring.clone.comment.dto;

import com.spring.clone.comment.Comment;
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

    public static CommentResponseDto getCommentResponseDtoFrom(Comment comment) {
        return CommentResponseDto.builder()
                .createdAt(comment.getCreatedAt())
                .commentId(comment.getId())
                .content(comment.getContent())
                .userId(comment.getUser().getUserId())
                .userImageUrl(comment.getUser().getImageUrl())
                .firstName(comment.getUser().getFirstName())
                .lastName(comment.getUser().getLastName())
                .build();
    }
}
