package com.spring.clone.comment.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CommentRequestDto {
    private Long postId;
    private String content;
}
