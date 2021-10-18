package com.spring.clone.post.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PostRequestDto {
    private String content;
    private String imageUrl;
}
