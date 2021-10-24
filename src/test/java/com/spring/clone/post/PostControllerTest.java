package com.spring.clone.post;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.clone.exception.PostNotFoundException;
import com.spring.clone.post.dto.PostRequestDto;
import com.spring.clone.post.dto.PostResponseDto;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

@WebMvcTest(controllers = PostController.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WebSecurityConfig.class))
class PostControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PostService postService;

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
        mockPostList.add(Post.of(
                new PostRequestDto("test content 1", "/image/img.img"), testUser));
        mockPostList.add(Post.of(
                new PostRequestDto("test content 2", "/image/img.img"), testUser));
        mockPostList.add(Post.of(
                new PostRequestDto("test content 3", "/image/img.img"), testUser));
        mockPostList.add(Post.of(
                new PostRequestDto("test content 4", "/image/img.img"), testUser));
        mockPostList.add(Post.of(
                new PostRequestDto("test content 5", "/image/img.img"), testUser));
        mockPostList.add(Post.of(
                new PostRequestDto("test content 6", "/image/img.img"), testUser));

    }

    private void authenticated() {
        Authentication authentication = new UsernamePasswordAuthenticationToken(mockUserDetails, "", mockUserDetails.getAuthorities());
        securityContext = SecurityContextHolder.getContext();
        securityContext.setAuthentication(authentication);
    }

    @Nested
    @DisplayName("Get 요청")
    class HttpGet {
        @Nested
        @DisplayName("Get 요청 성공")
        class GetSuccess {
            @Test
            @DisplayName("Get /post")
            void getPostsOrderByCreatedAtDesc() throws Exception {
                //given
                authenticated();
                Map<String, Object> result = new HashMap<>();
                List<PostResponseDto> responseDtoList = new ArrayList<>();
                mockPostList.stream().map(post -> post.toPostResponseDto(mockUserDetails))
                        .forEach(responseDtoList::add);
                result.put("posts", responseDtoList);

                given(postService.getPostsOrderByCreatedAtDesc(mockUserDetails))
                        .willReturn(result);

                //when
                mvc.perform(get("/post"))
                        .andDo(print())
                        //then
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.posts[0].content").value("test content 1"))
                        .andExpect(jsonPath("$.posts[0].imageUrl").value("/image/img.img"))
                        .andExpect(jsonPath("$.posts[0].firstName").value("tester"))
                        .andExpect(jsonPath("$.posts[0].lastName").value("test"))
                        .andExpect(jsonPath("$.posts[0].likeCount").value(0))
                        .andExpect(jsonPath("$.posts[0].commentCount").value(0))
                        .andExpect(jsonPath("$.posts[0].liked").value(false))
                        .andExpect(jsonPath("$.userImageUrl").value("https://district93.org/wp-content/uploads/2017/07/icon-user-default.png"))
                        .andExpect(jsonPath("$.statusCode").value(200))
                        .andExpect(jsonPath("$.username").value("testtester"));

                verify(postService).getPostsOrderByCreatedAtDesc(mockUserDetails);
            }

            @Test
            @WithUserDetails
            @DisplayName("로그인하지않은 사용자")
            void getPostsOrderByCreatedAtDescWithNoLogin() throws Exception {
                //given
                Map<String, Object> result = new HashMap<>();
                List<PostResponseDto> responseDtoList = new ArrayList<>();
                mockPostList.stream().map(post -> post.toPostResponseDto(null))
                        .forEach(responseDtoList::add);
                result.put("posts", responseDtoList);

                given(postService.getPostsOrderByCreatedAtDesc(null))
                        .willReturn(result);

                //when
                mvc.perform(get("/post"))
                        .andDo(print())
                        //then
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.posts[0].content").value("test content 1"))
                        .andExpect(jsonPath("$.posts[0].imageUrl").value("/image/img.img"))
                        .andExpect(jsonPath("$.posts[0].firstName").value("tester"))
                        .andExpect(jsonPath("$.posts[0].lastName").value("test"))
                        .andExpect(jsonPath("$.posts[0].likeCount").value(0))
                        .andExpect(jsonPath("$.posts[0].commentCount").value(0))
                        .andExpect(jsonPath("$.posts[0].liked").value(false))
                        .andExpect(jsonPath("$.userImageUrl").doesNotExist())
                        .andExpect(jsonPath("$.statusCode").value(200))
                        .andExpect(jsonPath("$.username").value("guest"));

                verify(postService).getPostsOrderByCreatedAtDesc(null);
            }
        }
    }

    @Nested
    @DisplayName("Post 요청")
    class HttpPost {
        @Nested
        @DisplayName("Post 요청 성공")
        class PostSuccess {
            @Test
            @DisplayName("Post /post")
            void addPost() throws Exception {
                //given
                authenticated();
                PostRequestDto requestDto = new PostRequestDto("test content", "/test.img");
                Post post = new Post(requestDto.getContent(), requestDto.getImageUrl(), testUser);
                post.setId(1L);
                String json = objectMapper.writeValueAsString(requestDto);
                given(postService.addPost(any(PostRequestDto.class), eq(testUser)))
                        .willReturn(post);

                //when
                mvc.perform(post("/post")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json))
                        .andDo(print())

                        //then
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.statusCode").value(200))
                        .andExpect(jsonPath("$.post.postId").value(1L))
                        .andExpect(jsonPath("$.post.content").value(requestDto.getContent()))
                        .andExpect(jsonPath("$.post.imageUrl").value(requestDto.getImageUrl()))
                        .andExpect(jsonPath("$.post.firstName").value(testUser.getFirstName()))
                        .andExpect(jsonPath("$.post.lastName").value(testUser.getLastName()))
                        .andExpect(jsonPath("$.post.likeCount").value(0))
                        .andExpect(jsonPath("$.post.commentCount").value(0))
                        .andExpect(jsonPath("$.post.liked").value(false))
                        .andExpect(jsonPath("$.post.comments").isArray());

                verify(postService).addPost(any(PostRequestDto.class), eq(testUser));
            }

            @Test
            @DisplayName("Post /post/{postId}/like")
            void likePost() throws Exception {
                //given
                authenticated();
                given(postService.toggleLikeInfo(1L, testUser)).willReturn(true);

                //when
                mvc.perform(post("/post/1/like")
                                .with(csrf()))

                        //then
                        .andDo(print())
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.isLiked").value(true))
                        .andExpect(jsonPath("$.statusCode").value(200));

                verify(postService).toggleLikeInfo(1L, testUser);
            }
        }

        @Nested
        @DisplayName("Post 요청 실패")
        class PostFail {
            @Test
            @DisplayName("media type 미정의")
            void addPostInvalidMediaType() throws Exception {
                //given
                authenticated();
                PostRequestDto requestDto = new PostRequestDto("test content", "/test.img");
                String json = objectMapper.writeValueAsString(requestDto);

                //when
                mvc.perform(post("/post")
                                .with(csrf())
                                .content(json))
                        .andDo(print())

                        //then
                        .andExpect(status().isUnsupportedMediaType());

                verify(postService, never()).addPost(any(), any());
            }

            @Test
            @DisplayName("요청 데이터 없음")
            void addPostNoData() throws Exception {
                //given
                authenticated();

                //when
                mvc.perform(post("/post")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON))
                        .andDo(print())

                        //then
                        .andExpect(status().isBadRequest());

                verify(postService, never()).addPost(any(), any());
            }

            @Test
            @WithUserDetails
            @DisplayName("로그인하지 않은 사용자")
            void addPostNoLogin() throws Exception {
                //given
                PostRequestDto requestDto = new PostRequestDto("test content", "/test.img");
                String json = objectMapper.writeValueAsString(requestDto);

                //when
                mvc.perform(post("/post")
                                .with(csrf())
                                .content(json)
                                .contentType(MediaType.APPLICATION_JSON))
                        .andDo(print())

                        //then
                        .andExpect(status().isUnauthorized())
                        .andExpect(jsonPath("$.statusCode").value(401));

                verify(postService, never()).addPost(any(), any());
            }

            @Test
            @WithUserDetails
            @DisplayName("로그인하지 않은 사용자 like")
            void likePostWithNoLogin() throws Exception {
                //given

                //when
                mvc.perform(post("/post/1/like")
                                .with(csrf()))
                        .andDo(print())

                        //then
                        .andExpect(status().isUnauthorized())
                        .andExpect(jsonPath("$.statusCode").value(401));


                verify(postService, never()).toggleLikeInfo(1L, testUser);
            }

            @Test
            @DisplayName("없는 게시글에 like")
            void likePost() throws Exception {
                //given
                authenticated();
                given(postService.toggleLikeInfo(1L, testUser))
                        .willThrow(PostNotFoundException.class);

                //when
                mvc.perform(post("/post/1/like")
                                .with(csrf()))
                        .andDo(print())

                        //then
                        .andExpect(status().isNotFound())
                        .andExpect(jsonPath("$.statusCode").value(404));


                verify(postService).toggleLikeInfo(1L, testUser);
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
            @DisplayName("Put /post/{postId}")
            void editPost() throws Exception {
                //given
                PostRequestDto requestDto = new PostRequestDto("test content", "/test.img");
                Post post = new Post(requestDto.getContent(), requestDto.getImageUrl(), testUser);
                post.setCreatedAt(LocalDateTime.now());
                String json = objectMapper.writeValueAsString(requestDto);
                given(postService.editPost(eq(1L), any(), eq(testUser)))
                        .willReturn(post);
                authenticated();

                //when
                mvc.perform(put("/post/1")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json))
                        .andDo(print())

                        //then
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.statusCode").value(200));

                verify(postService).editPost(eq(1L), any(PostRequestDto.class), eq(testUser));
            }
        }

        @Nested
        @DisplayName("Put 요청 실패")
        class PutFail {
            @Test
            @WithUserDetails
            @DisplayName("로그인하지않은 사용자")
            void editPostNoLogin() throws Exception {
                //given
                PostRequestDto requestDto = new PostRequestDto("test content", "/test.img");
                String json = objectMapper.writeValueAsString(requestDto);

                //when
                mvc.perform(put("/post/1")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json))
                        .andDo(print())

                        //then
                        .andExpect(status().isUnauthorized())
                        .andExpect(jsonPath("$.statusCode").value(401));

                verify(postService, never()).editPost(any(), any(), any());
            }

            @Test
            @DisplayName("media type 미정의")
            void editPostNoMediaType() throws Exception {
                //given
                PostRequestDto requestDto = new PostRequestDto("test content", "/test.img");
                String json = objectMapper.writeValueAsString(requestDto);
                authenticated();

                //when
                mvc.perform(put("/post/1")
                                .with(csrf())
                                .content(json))
                        .andDo(print())

                        //then
                        .andExpect(status().isUnsupportedMediaType());

                verify(postService, never()).editPost(any(), any(), any());
            }

            @Test
            @DisplayName("요청 데이터 없음")
            void editPostNoData() throws Exception {
                //given
                authenticated();

                //when
                mvc.perform(put("/post/1")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON))
                        .andDo(print())

                        //then
                        .andExpect(status().isBadRequest());

                verify(postService, never()).editPost(any(), any(), any());
            }

            @Test
            @DisplayName("권한 없음")
            void editPostNoPermission() throws Exception {
                //given
                PostRequestDto requestDto = new PostRequestDto("test content", "/test.img");
                String json = objectMapper.writeValueAsString(requestDto);
                authenticated();
                willThrow(AccessDeniedException.class).given(postService)
                        .editPost(eq(1L), any(PostRequestDto.class), eq(testUser));

                //when
                mvc.perform(put("/post/1")
                                .with(csrf())
                                .content(json)
                                .contentType(MediaType.APPLICATION_JSON))
                        .andDo(print())

                        //then
                        .andExpect(status().isForbidden());

                verify(postService).editPost(eq(1L), any(PostRequestDto.class), eq(testUser));
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
            @DisplayName("Delete /post/{postId}")
            void deletePost() throws Exception {
                //given
                authenticated();

                //when
                mvc.perform(delete("/post/1")
                                .with(csrf()))
                        .andDo(print())

                        //then
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.statusCode").value(200));

                verify(postService).deletePost(1L, testUser);
            }
        }

        @Nested
        @DisplayName("Delete 요청 실패")
        class DeleteFail {
            @Test
            @WithUserDetails
            @DisplayName("로그인하지않은 사용자")
            void deletePostWithNoLogin() throws Exception {
                //given

                //when
                mvc.perform(delete("/post/1")
                                .with(csrf()))
                        .andDo(print())

                        //then
                        .andExpect(status().isUnauthorized())
                        .andExpect(jsonPath("$.statusCode").value(401));

                verify(postService, never()).deletePost(any(), any());
            }

            @Test
            @DisplayName("없는 게시글 삭제")
            void deletePostNotExist() throws Exception {
                //given
                authenticated();
                willThrow(PostNotFoundException.class)
                        .given(postService).deletePost(1L, testUser);

                //when
                mvc.perform(delete("/post/1")
                                .with(csrf()))
                        .andDo(print())

                        //then
                        .andExpect(status().isNotFound())
                        .andExpect(jsonPath("$.statusCode").value(404));

                verify(postService).deletePost(1L, testUser);
            }

            @Test
            @DisplayName("권한 없음")
            void deletePostNotPermission() throws Exception {
                //given
                authenticated();
                willThrow(AccessDeniedException.class)
                        .given(postService).deletePost(1L, testUser);

                //when
                mvc.perform(delete("/post/1")
                                .with(csrf()))
                        .andDo(print())

                        //then
                        .andExpect(status().isForbidden())
                        .andExpect(jsonPath("$.statusCode").value(403));

                verify(postService).deletePost(1L, testUser);
            }
        }
    }
}