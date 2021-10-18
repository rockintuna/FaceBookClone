package com.spring.clone.post.dto;

import com.spring.clone.comment.dto.CommentResponseDto;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
public class PostResponseDto {
    private Long postId;
    private String content;
    private String imageUrl;
    private LocalDateTime createdAt;
    private String firstName;
    private String lastName;
    private Integer likeCount;
    private Integer commentCount;
    private boolean isLiked;
    private List<CommentResponseDto> commentResponseDtoList;
}
