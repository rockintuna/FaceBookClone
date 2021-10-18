package com.spring.clone.comment;

import com.spring.clone.comment.dto.CommentRequestDto;
import com.spring.clone.sercurity.UserDetailsImpl;
import com.spring.clone.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/comment")
    public Map<String, Object> addComment(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody CommentRequestDto requestDto) {
        Map<String, Object> result = new HashMap<>();
        User user = userDetails.getUser();
        commentService.addComment(requestDto, user);

        result.put("data", null);
        return result;
    }

    @PutMapping("/comment/{commentId}")
    public Map<String, Object> editComment(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable("commentId") Long commentId,
            @RequestBody CommentRequestDto requestDto) {
        User user = userDetails.getUser();
        Map<String, Object> result = new HashMap<>();
        commentService.editComment(commentId, requestDto, user);

        result.put("data", null);
        return result;
    }

    @DeleteMapping("/comment/{commentId}")
    public Map<String, Object> deletePost(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable("commentId") Long commentId
    ) {
        User user = userDetails.getUser();
        Map<String, Object> result = new HashMap<>();
        commentService.deleteComment(commentId, user);

        result.put("data", null);
        return result;
    }
}
