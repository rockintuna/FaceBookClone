package com.spring.clone.comment;

import com.spring.clone.comment.dto.CommentRequestDto;
import com.spring.clone.post.dto.PostRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/comment")
    public Map<String, Object> addComment(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody CommentRequestDto requestDto) {
        Map<String, Object> result = new HashMap<>();
//        todo Get User
//        User user = userDetails.getUser();
        commentService.addComment(requestDto, user);

        result.put("data", null);
        return result;
    }

    @PutMapping("/comment/{commentId}")
    public Map<String, Object> editComment(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable("postId") Long commentId,
            @RequestBody CommentRequestDto requestDto) {
        Map<String, Object> result = new HashMap<>();
        commentService.editComment(commentId, requestDto);

        result.put("data", null);
        return result;
    }

    @DeleteMapping
    public Map<String, Object> deletePost(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable("postId") Long postId
    ) {
        Map<String, Object> result = new HashMap<>();
        commentService.deleteComment(commentId, requestDto);

        result.put("data", null);
        return result;
    }
}
