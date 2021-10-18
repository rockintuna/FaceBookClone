package com.spring.clone.post.dto;

import com.spring.clone.comment.dto.CommentResponseDto;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class PostResponseDto {
    private String postId;
    private String content;
    private String firstName;
    private String lastName;
    private Long likeCount;
    private boolean isLiked;
    private List<CommentResponseDto> commentResponseDtoList;
}
