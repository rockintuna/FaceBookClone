package com.spring.clone.user.dto;

import com.spring.clone.sercurity.UserDetailsImpl;
import com.spring.clone.user.User;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
public class UserResponseDto {

    private String userId;
    private String firstName;
    private String lastName;
    private String imageUrl;

    public static UserResponseDto of(User user) {
        return UserResponseDto.builder()
                .userId(user.getUserId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .imageUrl(user.getImageUrl())
                .build();
    }

    public static List<UserResponseDto> listOf(List<User> users, UserDetailsImpl userDetails) {
        List<UserResponseDto> responseDtoList = new ArrayList<>();

        if ( userDetails == null ) {
            users.stream().map(UserResponseDto::of)
                    .forEach(responseDtoList::add);
        } else {
            users.stream().map(UserResponseDto::of)
                    .filter(responseDto -> !responseDto.getUserId().equals(userDetails.getUsername()))
                    .forEach(responseDtoList::add);
        }
        return responseDtoList;
    }
}
