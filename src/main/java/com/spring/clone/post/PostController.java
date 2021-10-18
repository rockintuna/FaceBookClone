package com.spring.clone.post;

import com.spring.clone.post.dto.PostRequestDto;
import com.spring.clone.post.dto.PostResponseDto;
import com.spring.clone.sercurity.UserDetailsImpl;
import com.spring.clone.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @GetMapping("/post")
    public Map<String, Object> getPostsOrderByCreatedAtDesc(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam("page") Integer page
    ) {
        Map<String, Object> result =
                new HashMap<>(postService.getPostsOrderByCreatedAtDesc(page - 1, userDetails));
        result.put("statusCode", 200);
        result.put("responseMessage", "게시글 조회 성공");
        return result;
    }

    @PostMapping("/post")
    public Map<String, Object> addPost(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody PostRequestDto requestDto) {
        Map<String, Object> result = new HashMap<>();
        User user = userDetails.getUser();
        postService.addPost(requestDto, user);

        result.put("statusCode", 200);
        result.put("responseMessage", "게시글 작성 성공");
        return result;
    }

    @PutMapping("/post/{postId}")
    public Map<String, Object> editPost(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable("postId") Long postId,
            @RequestBody PostRequestDto requestDto) {
        Map<String, Object> result = new HashMap<>();
        User user = userDetails.getUser();
        postService.editPost(postId, requestDto, user);

        result.put("statusCode", 200);
        result.put("responseMessage", "게시글 수정 성공");
        return result;
    }

    @DeleteMapping("/post/{postId}")
    public Map<String, Object> deletePost(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable("postId") Long postId
    ) {
        User user = userDetails.getUser();
        Map<String, Object> result = new HashMap<>();
        postService.deletePost(postId, user);

        result.put("statusCode", 200);
        result.put("responseMessage", "게시글 삭제 성공");
        return result;
    }

}
