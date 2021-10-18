package com.spring.clone.post;

import com.spring.clone.global.entity.Timestamped;
import com.spring.clone.post.dto.PostRequestDto;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
public class Post extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;
    private String imageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID", nullable = false)
    private User user;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID", nullable = false)
    private LikeInfo likeInfo;

//    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
//    private Comment comment;


    public Post(String content, String imageUrl, User user) {
        this.content = content;
        this.imageUrl = imageUrl;
        this.user = user;
    }

    public static Post of(PostRequestDto requestDto, User user) {
        return new Post(requestDto.getContent(),
                requestDto.getImgUrl(),
                user);
    }

    public void update(PostRequestDto requestDto) {
        this.content = requestDto.getContent();
        this.imageUrl = requestDto.getImgUrl();
    }
}
