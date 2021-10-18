package com.spring.clone.post;

import com.spring.clone.post.dto.PostRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @GetMapping("/post")
    public Map<String, Object> getPostsOrderByCreatedAtDesc(
            @RequestParam("page") Integer page
    ) {
        Map<String, Object> result = new HashMap<>();
        Page<Post> posts = postService.getPostsOrderByCreatedAtDesc(page);

        result.put("posts", posts);
        return result;
    }

    @PostMapping("/post")
    public Map<String, Object> addPost(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody PostRequestDto requestDto) {
        Map<String, Object> result = new HashMap<>();
//        todo Get User
//        User user = userDetails.getUser();
        postService.addPost(requestDto, user);

        result.put("data", null);
        return result;
    }

    @PutMapping("/post/{postId}")
    public Map<String, Object> editPost(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable("postId") Long postId,
            @RequestBody PostRequestDto requestDto) {
        Map<String, Object> result = new HashMap<>();
        //        todo Get User
//        User user = userDetails.getUser();
        postService.editPost(postId, requestDto, user);

        result.put("data", null);
        return result;
    }

    @DeleteMapping
    public Map<String, Object> deletePost(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable("postId") Long postId
    ) {
        //        todo Get User
//        User user = userDetails.getUser();
        Map<String, Object> result = new HashMap<>();
        postService.deletePost(postId, user);

        result.put("data", null);
        return result;
    }

}
