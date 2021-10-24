package com.spring.clone.comment;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.clone.comment.dto.CommentRequestDto;
import com.spring.clone.exception.CommentNotFoundException;
import com.spring.clone.exception.PostNotFoundException;
import com.spring.clone.post.Post;
import com.spring.clone.post.dto.PostRequestDto;
import com.spring.clone.sercurity.UserDetailsImpl;
import com.spring.clone.sercurity.WebSecurityConfig;
import com.spring.clone.user.User;
import com.spring.clone.user.dto.SignUpRequestDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = CommentController.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WebSecurityConfig.class))
class CommentControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CommentService commentService;

    private List<Post> mockPostList = new ArrayList<>();
    User testUser;
    UserDetailsImpl mockUserDetails;
    SecurityContext securityContext;

    @BeforeEach
    private void beforeEach() {
        SignUpRequestDto requestDto = new SignUpRequestDto(
                "tester@test.com", "password", "tester", "test", LocalDate.now(), "man", false, null);
        testUser = new User(requestDto);
        mockUserDetails = new UserDetailsImpl(testUser);
        //given
        for (int i = 1; i < 6; i++) {
            Post post = Post.of(new PostRequestDto("test content " + i, "/image/img.img"), testUser);
            post.setId((long) i);
            mockPostList.add(post);
        }
        for (int i = 1; i < 6; i++) {
            Comment comment = new Comment("test comment " + i, mockPostList.get(0), testUser);
        }
    }

    private void authenticated() {
        Authentication authentication = new UsernamePasswordAuthenticationToken(mockUserDetails, "", mockUserDetails.getAuthorities());
        securityContext = SecurityContextHolder.getContext();
        securityContext.setAuthentication(authentication);
    }

    @Nested
    @DisplayName("Post 요청")
    class HttpPost {
        @Nested
        @DisplayName("Post 요청 성공")
        class PostSuccess {
            @Test
            @DisplayName("Post /comment")
            void addComment() throws Exception {
                //given
                authenticated();
                CommentRequestDto requestDto = new CommentRequestDto();
                requestDto.setContent("test comment");
                requestDto.setPostId(1L);
                String json = objectMapper.writeValueAsString(requestDto);
                Comment comment = new Comment(requestDto.getContent(), mockPostList.get(0), testUser);
                comment.setCreatedAt(LocalDateTime.now());
                given(commentService.addComment(any(CommentRequestDto.class), eq(testUser)))
                        .willReturn(comment);

                //when
                mvc.perform(post("/comment")
                                .with(csrf())
                                .content(json)
                                .contentType(MediaType.APPLICATION_JSON))
                        .andDo(print())

                        //then
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.statusCode").value(200))
                        .andExpect(jsonPath("$.comment.content").value(requestDto.getContent()))
                        .andExpect(jsonPath("$.comment.userId").value(testUser.getUserId()))
                        .andExpect(jsonPath("$.comment.userImageUrl").value(testUser.getImageUrl()))
                        .andExpect(jsonPath("$.comment.firstName").value(testUser.getFirstName()))
                        .andExpect(jsonPath("$.comment.lastName").value(testUser.getLastName()));

                verify(commentService).addComment(any(CommentRequestDto.class), eq(testUser));
            }
        }

        @Nested
        @DisplayName("Post 요청 실패")
        class PostFail {
            @Test
            @WithUserDetails
            @DisplayName("media type 미정의")
            void addCommentInvalidMediaType() throws Exception {
                //given
                authenticated();
                CommentRequestDto requestDto = new CommentRequestDto();
                requestDto.setContent("test comment");
                requestDto.setPostId(1L);
                String json = objectMapper.writeValueAsString(requestDto);

                //when
                mvc.perform(post("/comment")
                                .with(csrf())
                                .content(json))
                        .andDo(print())

                        //then
                        .andExpect(status().isUnsupportedMediaType());

                verify(commentService, never()).addComment(any(), any());
            }

            @Test
            @DisplayName("요청 데이터 없음")
            void addCommentNoData() throws Exception {
                //given
                authenticated();

                //when
                mvc.perform(post("/comment")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON))
                        .andDo(print())

                        //then
                        .andExpect(status().isBadRequest());

                verify(commentService, never()).addComment(any(), any());
            }

            @Test
            @WithUserDetails
            @DisplayName("로그인하지않은 사용자")
            void addCommentNoLogin() throws Exception {
                //given
                CommentRequestDto requestDto = new CommentRequestDto();
                requestDto.setContent("test comment");
                requestDto.setPostId(1L);
                String json = objectMapper.writeValueAsString(requestDto);

                //when
                mvc.perform(post("/comment")
                                .with(csrf())
                                .content(json)
                                .contentType(MediaType.APPLICATION_JSON))
                        .andDo(print())

                        //then
                        .andExpect(status().isUnauthorized())
                        .andExpect(jsonPath("$.statusCode").value(401));

                verify(commentService, never()).addComment(any(), any());
            }

            @Test
            @DisplayName("존재하지 않는 게시글")
            void addCommentNoPost() throws Exception {
                //given
                authenticated();
                CommentRequestDto requestDto = new CommentRequestDto();
                requestDto.setContent("test comment");
                requestDto.setPostId(1L);
                String json = objectMapper.writeValueAsString(requestDto);
                given(commentService.addComment(any(CommentRequestDto.class), eq(testUser)))
                        .willThrow(PostNotFoundException.class);

                //when
                mvc.perform(post("/comment")
                                .with(csrf())
                                .content(json)
                                .contentType(MediaType.APPLICATION_JSON))
                        .andDo(print())

                        //then
                        .andExpect(status().isNotFound())
                        .andExpect(jsonPath("$.statusCode").value(404));


                verify(commentService).addComment(any(CommentRequestDto.class), eq(testUser));
            }
        }
    }

    @Nested
    @DisplayName("Put 요청")
    class HttpPut {
        @Nested
        @DisplayName("Put 요청 성공")
        class PutSuccess {
            @Test
            @DisplayName("Put /comment/{commentId}")
            void editComment() throws Exception {
                //given
                authenticated();
                CommentRequestDto requestDto = new CommentRequestDto();
                requestDto.setContent("test comment");
                requestDto.setPostId(1L);
                String json = objectMapper.writeValueAsString(requestDto);
                Comment comment = new Comment(requestDto.getContent(), mockPostList.get(0), testUser);
                comment.setCreatedAt(LocalDateTime.now());
                given(commentService.editComment(eq(1L), any(CommentRequestDto.class), eq(testUser)))
                        .willReturn(comment);

                //when
                mvc.perform(put("/comment/1")
                                .with(csrf())
                                .content(json)
                                .contentType(MediaType.APPLICATION_JSON))
                        .andDo(print())

                        //then
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.statusCode").value(200))
                        .andExpect(jsonPath("$.comment.content").value(requestDto.getContent()))
                        .andExpect(jsonPath("$.comment.userId").value(testUser.getUserId()))
                        .andExpect(jsonPath("$.comment.userImageUrl").value(testUser.getImageUrl()))
                        .andExpect(jsonPath("$.comment.firstName").value(testUser.getFirstName()))
                        .andExpect(jsonPath("$.comment.lastName").value(testUser.getLastName()));

                verify(commentService).editComment(eq(1L), any(CommentRequestDto.class), eq(testUser));
            }
        }

        @Nested
        @DisplayName("Put 요청 실패")
        class PutFail {
            @Test
            @WithUserDetails
            @DisplayName("로그인하지않은 사용자")
            void editCommentNoLogin() throws Exception {
                //given
                CommentRequestDto requestDto = new CommentRequestDto();
                requestDto.setContent("test comment");
                requestDto.setPostId(1L);
                String json = objectMapper.writeValueAsString(requestDto);

                //when
                mvc.perform(put("/comment/1")
                                .with(csrf())
                                .content(json)
                                .contentType(MediaType.APPLICATION_JSON))
                        .andDo(print())

                        //then
                        .andExpect(status().isUnauthorized())
                        .andExpect(jsonPath("$.statusCode").value(401));


                verify(commentService, never()).editComment(any(), any(), any());
            }

            @Test
            @DisplayName("media type 미정의")
            void editCommentNoMediaType() throws Exception {
                //given
                authenticated();
                CommentRequestDto requestDto = new CommentRequestDto();
                requestDto.setContent("test comment");
                requestDto.setPostId(1L);
                String json = objectMapper.writeValueAsString(requestDto);

                //when
                mvc.perform(put("/comment/1")
                                .with(csrf())
                                .content(json))
                        .andDo(print())

                        //then
                        .andExpect(status().isUnsupportedMediaType());


                verify(commentService, never()).editComment(any(), any(), any());
            }

            @Test
            @DisplayName("요청 데이터 없음")
            void editCommentNoData() throws Exception {
                //given
                authenticated();

                //when
                mvc.perform(put("/comment/1")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON))
                        .andDo(print())

                        //then
                        .andExpect(status().isBadRequest());


                verify(commentService, never()).editComment(any(), any(), any());
            }

            @Test
            @DisplayName("댓글 없음")
            void editCommentNoComment() throws Exception {
                //given
                authenticated();
                CommentRequestDto requestDto = new CommentRequestDto();
                requestDto.setContent("test comment");
                requestDto.setPostId(1L);
                String json = objectMapper.writeValueAsString(requestDto);
                given(commentService.editComment(eq(1L), any(CommentRequestDto.class), eq(testUser)))
                        .willThrow(CommentNotFoundException.class);

                //when
                mvc.perform(put("/comment/1")
                                .with(csrf())
                                .content(json)
                                .contentType(MediaType.APPLICATION_JSON))
                        .andDo(print())

                        //then
                        .andExpect(status().isNotFound())
                        .andExpect(jsonPath("$.statusCode").value(404));

                verify(commentService).editComment(eq(1L), any(CommentRequestDto.class), eq(testUser));
            }

            @Test
            @DisplayName("댓글 없음")
            void editCommentNoPermission() throws Exception {
                //given
                authenticated();
                CommentRequestDto requestDto = new CommentRequestDto();
                requestDto.setContent("test comment");
                requestDto.setPostId(1L);
                String json = objectMapper.writeValueAsString(requestDto);
                given(commentService.editComment(eq(1L), any(CommentRequestDto.class), eq(testUser)))
                        .willThrow(AccessDeniedException.class);

                //when
                mvc.perform(put("/comment/1")
                                .with(csrf())
                                .content(json)
                                .contentType(MediaType.APPLICATION_JSON))
                        .andDo(print())

                        //then
                        .andExpect(status().isForbidden())
                        .andExpect(jsonPath("$.statusCode").value(403));

                verify(commentService).editComment(eq(1L), any(CommentRequestDto.class), eq(testUser));
            }
        }
    }

    @Nested
    @DisplayName("Delete 요청")
    class HttpDelete {
        @Nested
        @DisplayName("Delete 요청 성공")
        class DeleteSuccess {
            @Test
            void deletePost() throws Exception {
                //given
                authenticated();

                //when
                mvc.perform(delete("/comment/1")
                                .with(csrf()))
                        .andDo(print())

                        //then
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.statusCode").value(200));

                verify(commentService).deleteComment(1L, testUser);
            }
        }

        @Nested
        @DisplayName("Delete 요청 실패")
        class DeleteFail {
            @Test
            @WithUserDetails
            @DisplayName("로그인하지않은 사용자")
            void deleteCommentNoLogin() throws Exception {
                //given

                //when
                mvc.perform(delete("/comment/1")
                                .with(csrf()))
                        .andDo(print())

                        //then
                        .andExpect(status().isUnauthorized())
                        .andExpect(jsonPath("$.statusCode").value(401));


                verify(commentService, never()).deleteComment(any(), any());
            }

            @Test
            @DisplayName("댓글 없음")
            void deleteCommentNoComment() throws Exception {
                //given
                authenticated();

                willThrow(CommentNotFoundException.class)
                        .given(commentService).deleteComment(1L, testUser);

                //when
                mvc.perform(delete("/comment/1")
                                .with(csrf()))
                        .andDo(print())

                        //then
                        .andExpect(status().isNotFound())
                        .andExpect(jsonPath("$.statusCode").value(404));

                verify(commentService).deleteComment(1L, testUser);
            }

            @Test
            @DisplayName("댓글 없음")
            void editCommentNoComment() throws Exception {
                //given
                authenticated();

                willThrow(AccessDeniedException.class)
                        .given(commentService).deleteComment(1L, testUser);

                //when
                mvc.perform(delete("/comment/1")
                                .with(csrf()))
                        .andDo(print())

                        //then
                        .andExpect(status().isForbidden())
                        .andExpect(jsonPath("$.statusCode").value(403));

                verify(commentService).deleteComment(1L, testUser);
            }
        }
    }
}