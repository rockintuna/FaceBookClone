package com.spring.clone.post;

import com.spring.clone.comment.Comment;
import com.spring.clone.config.Timestamped;
import com.spring.clone.post.dto.PostRequestDto;
import com.spring.clone.post.dto.PostResponseDto;
import com.spring.clone.user.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Post extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;
    private String imageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID", nullable = false)
    private User user;

    @BatchSize(size = 100)
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<LikeInfo> likeInfoList = new ArrayList<>();

    @BatchSize(size = 100)
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<Comment> comment = new ArrayList<>();


    public Post(String content, String imageUrl, User user) {
        this.content = content;
        this.imageUrl = imageUrl;
        this.user = user;
    }

    public static Post of(PostRequestDto requestDto, User user) {
        return new Post(requestDto.getContent(),
                requestDto.getImageUrl(),
                user);
    }

    public void update(PostRequestDto requestDto) {
        this.content = requestDto.getContent();
        this.imageUrl = requestDto.getImageUrl();
    }

    public boolean isWritedBy(User user) {
        return this.user == user;
    }

    public PostResponseDto toPostResponseDto(UserDetails userDetails) {
        if ( userDetails == null ) {
            return PostResponseDto.builder()
                    .postId(this.id)
                    .content(this.content)
//                    .commentCount(this.comments.size())
                    .likeCount(this.likeInfoList.size())
                    .firstName(user.getFirstName())
                    .lastName(user.getLastName())
                    .isLiked(false)
                    .createdAt(this.getCreatedAt())
                    .build();
        } else {
            return PostResponseDto.builder()
                    .postId(this.id)
                    .content(this.content)
//                    .commentCount(this.comments.size())
                    .likeCount(this.likeInfoList.size())
                    .firstName(user.getFirstName())
                    .lastName(user.getLastName())
//                    .isLiked(false)
                    .isLiked(this.likeInfoList.stream()
                            .anyMatch(likeInfo ->
                                    likeInfo.getUser().getUserId().equals(userDetails.getUsername())))
                    .createdAt(this.getCreatedAt())
                    .build();
        }
    }
}
