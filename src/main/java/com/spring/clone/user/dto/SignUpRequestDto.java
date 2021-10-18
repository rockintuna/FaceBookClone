package com.spring.clone.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;


@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SignUpRequestDto {
    private String userId;
    private String pwd;
    private String pwdCheck;
    private String firstName;
    private String lastName;
    private LocalDate birth;
    private String sex;
    private boolean admin = false;
    private String adminToken = "";

//
//    private LocalDate birth() {
//        birth.format("yyyy-mm-dd");
//    }
}
