package com.spring.clone.post;

import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

public class LikeInfo {
    @ManyToOne
    @JoinColumn(name = "POST_ID", nullable = false)
    private Post post;

    @ManyToOne
    @JoinColumn(name = "USER_ID", nullable = false)
    private User user;
}
