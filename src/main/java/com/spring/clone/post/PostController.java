package com.spring.clone.post;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @GetMapping
    public Map<String, Object> getPostsOrderByCreatedAtDesc() {
        Map<String, Object> result = new HashMap<>();
        List<Post> posts = postService.getPostsOrderByCreatedAtDesc();

        result.put("posts", posts);
        return result;
    }
}
