package com.spring.clone.comment;

import com.spring.clone.comment.dto.CommentRequestDto;
import com.spring.clone.exception.CommentNotFoundException;
import com.spring.clone.post.Post;
import com.spring.clone.post.PostService;
import com.spring.clone.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostService postService;

    public Comment addComment(CommentRequestDto requestDto, User user) {
        Post post = postService.getPostById(requestDto.getPostId());
        Comment newComment = Comment.of(requestDto.getContent(), post, user);
        return commentRepository.save(newComment);
    }

    public Comment editComment(Long commentId, CommentRequestDto requestDto, User user) {
        Comment comment = getCommentById(commentId);
        if (comment.isWritedBy(user)) {
            comment.setContent(requestDto.getContent());
            return commentRepository.save(comment);
        } else {
            throw new AccessDeniedException("권한이 없습니다.");
        }
    }

    public Long deleteComment(Long commentId, User user) {
        Comment comment = getCommentById(commentId);
        Long postId = comment.getPost().getId();
        if (comment.isWritedBy(user)) {
            commentRepository.delete(comment);
            return postId;
        } else {
            throw new AccessDeniedException("권한이 없습니다.");
        }
    }

    private Comment getCommentById(Long commentId) {
        return commentRepository.findById(commentId).orElseThrow(
                () -> new CommentNotFoundException("댓글을 찾을 수 없습니다.")
        );
    }
}
