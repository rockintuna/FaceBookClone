package com.spring.clone.post;

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
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = PostController.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WebSecurityConfig.class))
class PostControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private PostService postService;

    private List<Post> mockPostList = new ArrayList<>();
    User testUser;
    UserDetailsImpl userDetails;

    @BeforeEach
    private void beforeEach() {

        SignUpRequestDto requestDto = new SignUpRequestDto(
                "tester@test.com","password","tester","test", LocalDate.now(),"man",false,null);
        testUser = new User(requestDto);
        userDetails = new UserDetailsImpl(testUser);
        //given
        mockPostList.add(Post.of(
                new PostRequestDto("test content 1","/image/img.img"), testUser));
        mockPostList.add(Post.of(
                new PostRequestDto("test content 2","/image/img.img"), testUser));
        mockPostList.add(Post.of(
                new PostRequestDto("test content 3","/image/img.img"), testUser));
        mockPostList.add(Post.of(
                new PostRequestDto("test content 4","/image/img.img"), testUser));
        mockPostList.add(Post.of(
                new PostRequestDto("test content 5","/image/img.img"), testUser));
        mockPostList.add(Post.of(
                new PostRequestDto("test content 6","/image/img.img"), testUser));

    }

    @Nested
    @DisplayName("Get 요청")
    class HttpGet {
        @Nested
        @DisplayName("Get 요청 성공")
        class GetSuccess {
            @Test
            @WithUserDetails
            void getPostsOrderByCreatedAtDesc() throws Exception {
                //given
                Map<String, Object> result = new HashMap<>();
                List<PostResponseDto> responseDtoList = new ArrayList<>();
                mockPostList.stream().map(post -> post.toPostResponseDto(null))
                        .forEach(responseDtoList::add);
                result.put("page", 1);
                result.put("totalPage", 2);
                result.put("posts", responseDtoList);

                given(postService.getPostsOrderByCreatedAtDesc(0, null))
                        .willReturn(result);

                //when
                mvc.perform(get("/post")
                                .param("page", "1"))
                        .andDo(print())
                        //then
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.totalPage").value(2))
                        .andExpect(jsonPath("$.page").value(1))
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

                verify(postService).getPostsOrderByCreatedAtDesc(0, null);
            }
        }
        @Nested
        @DisplayName("Get 요청 실패")
        class GetFail {
        }
    }
    @Nested
    @DisplayName("Post 요청")
    class HttpPost {
        @Nested
        @DisplayName("Post 요청 성공")
        class PostSuccess {
            @Test
            void addPost() {

            }
            @Test
            void likePost() {
            }
        }
        @Nested
        @DisplayName("Post 요청 실패")
        class PostFail {
        }
    }
    @Nested
    @DisplayName("Put 요청")
    class HttpPut {
        @Nested
        @DisplayName("Put 요청 성공")
        class PutSuccess {
            @Test
            void editPost() {
            }
        }
        @Nested
        @DisplayName("Put 요청 실패")
        class PutFail {
        }
    }
    @Nested
    @DisplayName("Delete 요청")
    class HttpDelete {
        @Nested
        @DisplayName("Put 요청 성공")
        class DeleteSuccess {
            @Test
            void deletePost() {
            }
        }
        @Nested
        @DisplayName("Delete 요청 실패")
        class DeleteFail {
        }
    }
}