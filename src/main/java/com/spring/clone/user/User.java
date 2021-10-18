package com.spring.clone.user;

import com.spring.clone.user.dto.SignUpRequestDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@NoArgsConstructor
@Setter
@Getter
public class User {

    // ID가 자동으로 생성 및 증가합니다.
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    private Long id;

    @Column(nullable = false, unique = true)
    private String userId;

    @Column(nullable = false)
    private String pwd;

    @Column(nullable = false,unique = true)
    private String firstName;

    @Column(nullable = false,unique = true)
    private String lastName;

    @Column(nullable = false,unique = true)
    private LocalDate birth;

    @Column(nullable = false,unique = true)
    private String sex;

    @Column(nullable = true)
    @Enumerated(value = EnumType.STRING)
    private UserRoleEnum role;


    public User(String userId, String pwd, String firstName, String lastName , String sex , LocalDate birth ,UserRoleEnum role) {
        this.userId = userId;
        this.pwd = pwd;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birth = birth;
        this.sex = sex;
        this.role = role;

    }
    public User(SignUpRequestDto requestDto) {
        this.userId = requestDto.getUserId();
        this.firstName = requestDto.getFirstName();
        this.sex = requestDto.getSex();
        this.lastName = requestDto.getLastName();
        this.pwd = requestDto.getPwd();
        this.birth = requestDto.getBirth();
    }
    public User(String userId, String pwd, UserRoleEnum role) {
        this.userId = userId;
        this.pwd = pwd;
        this.role = role;
    }
}
