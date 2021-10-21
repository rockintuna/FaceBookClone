package com.spring.clone.comment;

import com.spring.clone.comment.dto.CommentRequestDto;
import com.spring.clone.exception.CommentNotFoundException;
import com.spring.clone.exception.PostNotFoundException;
import com.spring.clone.post.Post;
import com.spring.clone.post.PostService;
import com.spring.clone.post.dto.PostRequestDto;
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
import org.springframework.security.access.AccessDeniedException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @InjectMocks
    private CommentService commentService;

    @Mock
    private PostService postService;
    @Mock
    private CommentRepository commentRepository;

    private List<Post> mockPostList = new ArrayList<>();
    User testUser;

    @BeforeEach
    private void beforeEach() {
        SignUpRequestDto requestDto = new SignUpRequestDto(
                "tester@test.com","password","tester","test", LocalDate.now(),"man",false,null);
        testUser = new User(requestDto);
        //given
        for (int i = 1; i < 6; i++) {
            Post post = Post.of(new PostRequestDto("test content "+i, "/image/img.img"), testUser);
            post.setId((long) i);
            mockPostList.add(post);
        }
        for (int i = 1; i < 6; i++) {
            Comment comment = new Comment("test comment "+i, mockPostList.get(0), testUser);
        }
    }

    @Nested
    @DisplayName("댓글 작성")
    class AddComment {
        @Nested
        @DisplayName("성공")
        class Success {
            @Test
            @DisplayName("댓글 작성 성공")
            void addComment() {
                //given
                CommentRequestDto requestDto = new CommentRequestDto();
                requestDto.setPostId(1L);
                requestDto.setContent("test comment");
                Comment mockComment = new Comment(requestDto.getContent(), mockPostList.get(0), testUser);
                given(postService.getPostById(requestDto.getPostId()))
                        .willReturn(mockPostList.get(0));
                given(commentRepository.save(any(Comment.class)))
                        .willReturn(mockComment);

                //when
                Comment comment = commentService.addComment(requestDto, testUser);

                //then
                assertThat(comment).isNotNull();
                assertThat(comment.getContent()).isEqualTo(mockComment.getContent());
                assertThat(comment.getUser()).isEqualTo(testUser);
                assertThat(comment.getPost().getId()).isEqualTo(mockPostList.get(0).getId());
                verify(postService).getPostById(requestDto.getPostId());
                verify(commentRepository).save(any(Comment.class));
            }
        }
        @Nested
        @DisplayName("실패")
        class Fail {
            @Test
            @DisplayName("댓글 작성 실패 게시글 없음")
            void addCommentNoPost() {
                //given
                CommentRequestDto requestDto = new CommentRequestDto();
                requestDto.setPostId(1L);
                requestDto.setContent("test comment");
                Comment mockComment = new Comment(requestDto.getContent(), mockPostList.get(0), testUser);
                given(postService.getPostById(requestDto.getPostId()))
                        .willThrow(PostNotFoundException.class);

                //when, then
                assertThrows(PostNotFoundException.class,
                        () -> commentService.addComment(requestDto, testUser));
                verify(postService).getPostById(requestDto.getPostId());
                verify(commentRepository, never()).save(any());
            }
        }
    }



    @Nested
    @DisplayName("댓글 작성")
    class EditComment {
        @Nested
        @DisplayName("성공")
        class Success {
            @Test
            @DisplayName("댓글 수정 성공")
            void editComment() {
                //given
                CommentRequestDto requestDto = new CommentRequestDto();
                requestDto.setPostId(1L);
                requestDto.setContent("test comment");
                Comment mockComment = new Comment(requestDto.getContent(), mockPostList.get(0), testUser);
                given(commentRepository.findById(1L))
                        .willReturn(Optional.of(mockComment));
                given(commentRepository.save(any(Comment.class)))
                        .willReturn(mockComment);

                //when
                Comment comment = commentService.editComment(1L, requestDto, testUser);

                //then
                assertThat(comment).isNotNull();
                assertThat(comment.getContent()).isEqualTo(mockComment.getContent());
                assertThat(comment.getUser()).isEqualTo(testUser);
                assertThat(comment.getPost().getId()).isEqualTo(mockPostList.get(0).getId());
                verify(commentRepository).findById(1L);
                verify(commentRepository).save(any(Comment.class));
            }
        }
        @Nested
        @DisplayName("실패")
        class Fail {
            @Test
            @DisplayName("댓글 없음")
            void editCommentNoComment() {
                //given
                CommentRequestDto requestDto = new CommentRequestDto();
                given(commentRepository.findById(1L))
                        .willReturn(Optional.empty());

                //when, then
                assertThrows(CommentNotFoundException.class,
                        () -> commentService.editComment(1L, requestDto, testUser));
                verify(commentRepository).findById(1L);
                verify(commentRepository, never()).save(any());
            }

            @Test
            @DisplayName("권한 없음")
            void editCommentNoPermission() {
                //given
                SignUpRequestDto signUpRequestDto = new SignUpRequestDto(
                        "older@test.com","password","older","test", LocalDate.now(),"man",false,null);
                User oldUser = new User(signUpRequestDto);
                CommentRequestDto requestDto = new CommentRequestDto();
                requestDto.setPostId(1L);
                requestDto.setContent("test comment");
                Comment mockComment = new Comment(requestDto.getContent(), mockPostList.get(0), oldUser);
                given(commentRepository.findById(1L))
                        .willReturn(Optional.of(mockComment));

                //when, then
                assertThrows(AccessDeniedException.class,
                        () -> commentService.editComment(1L, requestDto, testUser));
                verify(commentRepository).findById(1L);
                verify(commentRepository, never()).save(any());
            }
        }
    }



    @Nested
    @DisplayName("댓글 삭제")
    class DeleteComment {
        @Nested
        @DisplayName("성공")
        class Success {
            @Test
            @DisplayName("댓글 삭제 성공")
            void deleteComment() {
                //given
                CommentRequestDto requestDto = new CommentRequestDto();
                requestDto.setPostId(1L);
                requestDto.setContent("test comment");
                Comment mockComment = new Comment(requestDto.getContent(), mockPostList.get(0), testUser);
                given(commentRepository.findById(1L))
                        .willReturn(Optional.of(mockComment));

                //when
                Long postId = commentService.deleteComment(1L, testUser);

                //then
                assertThat(postId).isEqualTo(mockPostList.get(0).getId());
                verify(commentRepository).findById(1L);
                verify(commentRepository).delete(any(Comment.class));
            }
        }
        @Nested
        @DisplayName("실패")
        class Fail {
            @Test
            @DisplayName("댓글 없음")
            void deleteCommentNoComment() {
                //given
                given(commentRepository.findById(1L))
                        .willReturn(Optional.empty());

                //when, then
                assertThrows(CommentNotFoundException.class,
                        () -> commentService.deleteComment(1L, testUser));
                verify(commentRepository).findById(1L);
                verify(commentRepository, never()).delete(any());
            }
            @Test
            @DisplayName("권한 없음")
            void deleteCommentNoPermission() {
                //given
                SignUpRequestDto signUpRequestDto = new SignUpRequestDto(
                        "older@test.com","password","older","test", LocalDate.now(),"man",false,null);
                User oldUser = new User(signUpRequestDto);
                CommentRequestDto requestDto = new CommentRequestDto();
                requestDto.setPostId(1L);
                requestDto.setContent("test comment");
                Comment mockComment = new Comment(requestDto.getContent(), mockPostList.get(0), oldUser);
                given(commentRepository.findById(1L))
                        .willReturn(Optional.of(mockComment));

                //when, then
                assertThrows(AccessDeniedException.class,
                        () -> commentService.deleteComment(1L, testUser));
                verify(commentRepository).findById(1L);
                verify(commentRepository, never()).delete(any());
            }
        }
    }
}