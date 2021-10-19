package com.spring.clone.comment;

import com.spring.clone.comment.dto.CommentRequestDto;
import com.spring.clone.sercurity.UserDetailsImpl;
import com.spring.clone.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationServiceException;
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
        User user = getUserByUserdetailsIfExist(userDetails);
        commentService.addComment(requestDto, user);

        result.put("statusCode", 200);
        result.put("responseMessage", "댓글 생성 성공");
        return result;
    }

    @PutMapping("/comment/{commentId}")
    public Map<String, Object> editComment(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable("commentId") Long commentId,
            @RequestBody CommentRequestDto requestDto) {
        User user = getUserByUserdetailsIfExist(userDetails);
        Map<String, Object> result = new HashMap<>();
        commentService.editComment(commentId, requestDto, user);

        result.put("statusCode", 200);
        result.put("responseMessage", "댓글 수정 성공");
        return result;
    }

    @DeleteMapping("/comment/{commentId}")
    public Map<String, Object> deletePost(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable("commentId") Long commentId
    ) {
        User user = getUserByUserdetailsIfExist(userDetails);
        Map<String, Object> result = new HashMap<>();
        commentService.deleteComment(commentId, user);

        result.put("statusCode", 200);
        result.put("responseMessage", "댓글 삭제 성공");
        return result;
    }

    private User getUserByUserdetailsIfExist(UserDetailsImpl userDetails) {
        User user;
        if ( userDetails != null ) {
            user = userDetails.getUser();
        } else {
            throw new AuthenticationServiceException("로그인이 필요합니다.");
        }
        return user;
    }
}
