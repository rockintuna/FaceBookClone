package com.spring.clone.user;

import com.spring.clone.user.dto.SignUpRequestDto;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@NoArgsConstructor
@Setter
@Getter
@EqualsAndHashCode
public class User {

    // ID가 자동으로 생성 및 증가합니다.
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    private Long id;

    @Column(nullable = false, unique = true)
    private String userId;

    @Column(nullable = false)
    private String pwd;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false)
    private LocalDate birth;

    private String imageUrl;

    @Column(nullable = true)
    @Enumerated(value = EnumType.STRING)
    private Sex sex;



//
//    public User(String userId, String pwd, Sex sex ,String firstName, String lastName, LocalDate birth) {
//        this.userId = userId;
//        this.pwd = pwd;
//        this.firstName = firstName;
//        this.lastName = lastName;
//        this.birth = birth;
//        this.sex = sex;
//
//    }

    public User(SignUpRequestDto requestDto) {
        this.userId = requestDto.getUserId();
        this.firstName = requestDto.getFirstName();
        this.sex = Sex.typeOf(requestDto.getSex());
        this.lastName = requestDto.getLastName();
        this.pwd = requestDto.getPwd();
        this.birth = requestDto.getBirth();
    }


}
