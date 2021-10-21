package com.spring.clone.post.dto;

import com.spring.clone.comment.dto.CommentResponseDto;
import com.spring.clone.post.Post;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
public class PostCreateResponseDto {
    private Long postId;
    private String content;
    private String imageUrl;
    private LocalDateTime createdAt;
    private String firstName;
    private String lastName;
    @Builder.Default
    private Integer likeCount = 0;
    @Builder.Default
    private Integer commentCount = 0;
    @Builder.Default
    private boolean isLiked = false;
    @Builder.Default
    private List<CommentResponseDto> comments = new ArrayList<>();

    public static PostCreateResponseDto from(Post post) {
        return PostCreateResponseDto.builder()
                .postId(post.getId())
                .content(post.getContent())
                .imageUrl(post.getImageUrl())
                .createdAt(post.getCreatedAt())
                .firstName(post.getUser().getFirstName())
                .lastName(post.getUser().getLastName())
                .build();
    }
}
