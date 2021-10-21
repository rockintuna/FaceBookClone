package com.spring.clone.post;

import com.spring.clone.exception.PostNotFoundException;
import com.spring.clone.post.dto.PostRequestDto;
import com.spring.clone.post.dto.PostResponseDto;
import com.spring.clone.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final LikeInfoRepository likeInfoRepository;

    public Map<String, Object> getPostsOrderByCreatedAtDesc(Integer page, UserDetails userDetails) {
        Page<Post> postPage = postRepository.findAllByOrderByCreatedAtDesc(PageRequest.of(page, 5));
        Map<String, Object> postDataMap = new HashMap<>();

        postDataMap.put("page", postPage.getNumber()+1);
        postDataMap.put("totalPage", postPage.getTotalPages());
        postDataMap.put("posts", postPageToPostResponseDtoList(postPage, userDetails));

        return postDataMap;
    }

    private List<PostResponseDto> postPageToPostResponseDtoList(Page<Post> postPage,
                                                                UserDetails userDetails) {
        List<PostResponseDto> postResponseDtoList = new ArrayList<>();
        postPage.stream()
                .map(article -> article.toPostResponseDto(userDetails))
                .forEach(postResponseDtoList::add);
        return postResponseDtoList;
    }

    public Post addPost(PostRequestDto requestDto, User user) {
        Post newPost = Post.of(requestDto, user);
        return postRepository.save(newPost);
    }

    public Post editPost(Long postId, PostRequestDto requestDto, User user) {
        Post post = getPostById(postId);
        if ( post.isWritedBy(user) ) {
            post.update(requestDto);
            return postRepository.save(post);
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

    public boolean toggleLikeInfo(Long postId, User user) {
        Post post = getPostById(postId);
        Optional<LikeInfo> likeInfo = likeInfoRepository.findByPostIdAndUserId(postId, user.getId());
        if ( likeInfo.isPresent() ) {
            likeInfoRepository.delete(likeInfo.get());
            return false;
        } else {
            LikeInfo newLikeInfo = new LikeInfo(post, user);
            likeInfoRepository.save(newLikeInfo);
            return true;
        }
    }

    public Post getPostById(Long postId) {
        return postRepository.findById(postId).orElseThrow(
                () -> new PostNotFoundException("게시글을 찾을 수 없습니다.")
        );
    }
}
