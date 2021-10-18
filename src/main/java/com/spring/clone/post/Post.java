package com.spring.clone.post;

import com.spring.clone.global.entity.Timestamped;
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

}
