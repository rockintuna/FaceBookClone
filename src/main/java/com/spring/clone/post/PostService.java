package com.spring.clone.post;

import com.spring.clone.exception.PostNotFoundException;
import com.spring.clone.post.dto.PostRequestDto;
import com.spring.clone.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    public Page<Post> getPostsOrderByCreatedAtDesc(Integer page) {
        return postRepository.findAllByOrderByCreatedAtDesc(PageRequest.of(page, 5));
    }

    public void addPost(PostRequestDto requestDto, User user) {
        Post newPost = Post.of(requestDto, user);
        postRepository.save(newPost);
    }

    public void editPost(Long postId, PostRequestDto requestDto, User user) {
        Post post = getPostById(postId);
        if ( post.isWritedBy(user) ) {
            post.update(requestDto);
            postRepository.save(post);
        } else {
            throw new AccessDeniedException("권한이 없습니다.");
        }
    }

    public void deletePost(Long postId, User user) {
        Post post = getPostById(postId);
        if ( post.isWritedBy(user) ) {
            postRepository.delete(post);
        } else {
            throw new AccessDeniedException("권한이 없습니다.");
        }
    }

    public Post getPostById(Long postId) {
        return postRepository.findById(postId).orElseThrow(
                () -> new PostNotFoundException("게시글을 찾을 수 없습니다.")
        );
    }
}
