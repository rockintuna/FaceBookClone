package com.spring.clone.post;

import com.spring.clone.global.exception.PostNotFoundException;
import com.spring.clone.post.dto.PostRepository;
import com.spring.clone.post.dto.PostRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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

    public void editPost(Long postId, PostRequestDto requestDto) {
        Post post = getPostById(postId);
        post.update(requestDto);
        postRepository.save(post);
    }

    public void deletePost(Long postId) {
        Post post = getPostById(postId);
        postRepository.delete(post);
    }

    private Post getPostById(Long postId) {
        return postRepository.findById(postId).orElseThrow(
                () -> new PostNotFoundException("게시글을 찾을 수 없습니다.")
        );
    }
}
