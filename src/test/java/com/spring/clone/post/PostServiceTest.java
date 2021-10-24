package com.spring.clone.post;

import com.spring.clone.exception.PostNotFoundException;
import com.spring.clone.post.dto.PostRequestDto;
import com.spring.clone.post.dto.PostResponseDto;
import com.spring.clone.sercurity.UserDetailsImpl;
import com.spring.clone.user.User;
import com.spring.clone.user.dto.SignUpRequestDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContext;

import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @InjectMocks
    private PostService postService;

    @Mock
    private PostRepository postRepository;
    @Mock
    private LikeInfoRepository likeInfoRepository;

    private List<Post> mockPostList = new ArrayList<>();
    User testUser;
    UserDetailsImpl mockUserDetails;

    @BeforeEach
    private void beforeEach() {
        //given
        SignUpRequestDto requestDto = new SignUpRequestDto(
                "tester@test.com", "password", "tester", "test", LocalDate.now(), "man", false, null);
        testUser = new User(requestDto);
        mockUserDetails = new UserDetailsImpl(testUser);

        for (int i = 1; i < 6; i++) {
            Post post = Post.of(new PostRequestDto("test content " + i, "/image/img.img"), testUser);
            post.setId((long) i);
            mockPostList.add(post);
        }
    }

    @Nested
    @DisplayName("게시글 생성순 조회")
    class GetPostsOrderByCreatedAtDesc {
        @Nested
        @DisplayName("성공")
        class Success {
            @Test
            @DisplayName("게시글 생성순 조회 성공")
            void getPostsOrderByCreatedAtDesc() {
                //given
                given(postRepository.findAllByOrderByCreatedAtDesc())
                        .willReturn(mockPostList);

                //when
                Map<String, Object> result = postService.getPostsOrderByCreatedAtDesc(mockUserDetails);
                List<PostResponseDto> responseDtoList = (List<PostResponseDto>) result.get("posts");

                //then
                assertThat(result.get("posts")).isNotNull();
                assertThat(result.get("posts")).isInstanceOf(ArrayList.class);
                assertThat(responseDtoList.get(0).getContent()).isEqualTo("test content 1");
                assertThat(responseDtoList.get(0).getImageUrl()).isEqualTo("/image/img.img");
                assertThat(responseDtoList.get(0).getFirstName()).isEqualTo(testUser.getFirstName());
                assertThat(responseDtoList.get(0).getLastName()).isEqualTo(testUser.getLastName());
            }
        }
    }

    @Nested
    @DisplayName("게시글 생성")
    class AddPost {
        @Nested
        @DisplayName("성공")
        class Success {
            @Test
            @DisplayName("게시글 생성 성공")
            void addPost() {
                //given
                PostRequestDto postRequestDto = new PostRequestDto("test content", "/test.img");

                //when
                postService.addPost(postRequestDto, testUser);

                //then
                verify(postRepository).save(any(Post.class));
            }
        }

        @Nested
        @DisplayName("실패")
        class Fail {
            @Test
            @DisplayName("게시글 생성 실패 요청 데이터 없음")
            void addPostNoData() {
                //given
                PostRequestDto postRequestDto = null;

                //when, then
                assertThrows(NullPointerException.class,
                        () -> postService.addPost(postRequestDto, testUser));
                verify(postRepository, never()).save(any(Post.class));
            }
        }
    }

    @Nested
    @DisplayName("게시글 수정")
    class UpdatePost {
        @Nested
        @DisplayName("성공")
        class Success {
            @Test
            @DisplayName("게시글 수정 성공")
            void editPost() {
                //given
                PostRequestDto postRequestDto = new PostRequestDto("test content", "/test.img");
                Post post = new Post("old content", "/old.img", testUser);
                given(postRepository.findById(1L)).willReturn(Optional.of(post));

                //when
                postService.editPost(1L, postRequestDto, testUser);

                //then
                verify(postRepository).save(any(Post.class));
            }
        }

        @Nested
        @DisplayName("실패")
        class Fail {
            @Test
            @DisplayName("게시글 수정 실패 게시글 없음")
            void editPostNoPost() {
                //given
                PostRequestDto postRequestDto = new PostRequestDto("test content", "/test.img");
                given(postRepository.findById(1L)).willReturn(Optional.empty());

                //when, then
                assertThrows(PostNotFoundException.class,
                        () -> postService.editPost(1L, postRequestDto, testUser));
                verify(postRepository, never()).save(any(Post.class));
            }

            @Test
            @DisplayName("게시글 수정 실패 요청 데이터 없음")
            void editPostNoData() {
                //given
                PostRequestDto postRequestDto = null;
                Post post = new Post("old content", "/old.img", testUser);
                given(postRepository.findById(1L)).willReturn(Optional.of(post));

                //when, then
                assertThrows(NullPointerException.class,
                        () -> postService.editPost(1L, postRequestDto, testUser));
                verify(postRepository, never()).save(any(Post.class));
            }

            @Test
            @DisplayName("게시글 수정 실패 권한 없음")
            void editPostNoPermission() {
                //given
                SignUpRequestDto requestDto = new SignUpRequestDto(
                        "older@test.com", "password", "older", "test", LocalDate.now(), "man", false, null);
                User oldUser = new User(requestDto);
                PostRequestDto postRequestDto = new PostRequestDto("test content", "/test.img");
                Post post = new Post("old content", "/old.img", oldUser);
                given(postRepository.findById(1L)).willReturn(Optional.of(post));

                //when, then
                assertThrows(AccessDeniedException.class,
                        () -> postService.editPost(1L, postRequestDto, testUser));
                verify(postRepository, never()).save(any(Post.class));
            }
        }
    }

    @Nested
    @DisplayName("게시글 제거")
    class deletePost {
        @Nested
        @DisplayName("성공")
        class Success {
            @Test
            @DisplayName("게시글 삭제 성공")
            void deletePost() {
                //given
                Post post = new Post("old content", "/old.img", testUser);
                given(postRepository.findById(1L)).willReturn(Optional.of(post));

                //when
                postService.deletePost(1L, testUser);

                //then
                verify(postRepository).delete(any(Post.class));
            }
        }

        @Nested
        @DisplayName("실패")
        class Fail {
            @Test
            @DisplayName("게시글 삭제 실패 게시글 없음")
            void deletePostNoPost() {
                //given
                given(postRepository.findById(1L)).willReturn(Optional.empty());

                //when, then
                assertThrows(PostNotFoundException.class,
                        () -> postService.deletePost(1L, testUser));
                verify(postRepository, never()).delete(any(Post.class));
            }

            @Test
            @DisplayName("게시글 삭제 실패 권한 없음")
            void deletePostNoPermission() {
                //given
                SignUpRequestDto requestDto = new SignUpRequestDto(
                        "older@test.com", "password", "older", "test", LocalDate.now(), "man", false, null);
                User oldUser = new User(requestDto);
                PostRequestDto postRequestDto = new PostRequestDto("test content", "/test.img");
                Post post = new Post("old content", "/old.img", oldUser);
                given(postRepository.findById(1L)).willReturn(Optional.of(post));

                //when, then
                assertThrows(AccessDeniedException.class,
                        () -> postService.deletePost(1L, testUser));
                verify(postRepository, never()).delete(any(Post.class));
            }
        }
    }

    @Nested
    @DisplayName("게시글 좋아요 변경")
    class TogglelikePost {
        @Nested
        @DisplayName("성공")
        class Success {
            @Test
            @DisplayName("게시글 좋아요 성공")
            void toggleLikeInfoTrue() {
                //given
                Post post = new Post("old content", "/old.img", testUser);
                given(postRepository.findById(1L)).willReturn(Optional.of(post));
                given(likeInfoRepository.findByPostIdAndUserId(1L, testUser.getId()))
                        .willReturn(Optional.empty());

                //when
                boolean isLikeid = postService.toggleLikeInfo(1L, testUser);

                //then
                assertThat(isLikeid).isTrue();
                verify(postRepository).findById(1L);
                verify(likeInfoRepository).findByPostIdAndUserId(1L, testUser.getId());
                verify(likeInfoRepository).save(any(LikeInfo.class));
            }

            @Test
            @DisplayName("게시글 좋아요 취소 성공")
            void toggleLikeInfoFalse() {
                //given
                Post post = new Post("old content", "/old.img", testUser);
                LikeInfo likeInfo = new LikeInfo(post, testUser);
                given(postRepository.findById(1L)).willReturn(Optional.of(post));
                given(likeInfoRepository.findByPostIdAndUserId(1L, testUser.getId()))
                        .willReturn(Optional.of(likeInfo));

                //when
                boolean isLikeid = postService.toggleLikeInfo(1L, testUser);

                //then
                assertThat(isLikeid).isFalse();
                verify(postRepository).findById(1L);
                verify(likeInfoRepository).findByPostIdAndUserId(1L, testUser.getId());
                verify(likeInfoRepository).delete(any(LikeInfo.class));
            }
        }

        @Nested
        @DisplayName("실패")
        class Fail {
            @Test
            @DisplayName("좋아요 변경 실패 게시글 없음")
            void toggleLikeInfoNoPost() {
                //given
                Post post = new Post("old content", "/old.img", testUser);
                given(postRepository.findById(1L)).willReturn(Optional.empty());

                //when, then
                assertThrows(PostNotFoundException.class,
                        () -> postService.toggleLikeInfo(1L, testUser));
            }
        }
    }

    @Test
    @DisplayName("게시글 아이디 찾기 성공")
    void getPostById() {
        //given
        Post post = new Post("old content", "/old.img", testUser);
        given(postRepository.findById(1L)).willReturn(Optional.of(post));

        //when
        Post result = postService.getPostById(1L);

        //then
        assertThat(result.getContent()).isEqualTo("old content");
        assertThat(result.getImageUrl()).isEqualTo("/old.img");
        assertThat(result.getUser()).isEqualTo(testUser);
    }
}