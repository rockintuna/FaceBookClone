package com.spring.clone.user;

import com.spring.clone.exception.CloneException;
import com.spring.clone.exception.ErrorCode;
import com.spring.clone.sercurity.JwtTokenProvider;
import com.spring.clone.sercurity.UserDetailsImpl;
import com.spring.clone.user.dto.SignUpRequestDto;
import com.spring.clone.user.dto.UserImageUrlRequestDto;
import com.spring.clone.user.dto.UserRequestDto;
import com.spring.clone.user.dto.UserResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class UserController {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    public UserController(UserService userService, JwtTokenProvider jwtTokenProvider) {
        this.userService = userService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    //가입 요청 처리
    @PostMapping("/user/register")
    public Map<String, Object> registerUser(@RequestBody SignUpRequestDto requestDto) throws CloneException {
        User user = userService.registerUser(requestDto);
        Map<String, Object> result = new HashMap<>();
        result.put("result", "true");
        result.put("id", String.valueOf(user.getId()));
        result.put("userId", user.getUserId());
        result.put("firstName", user.getFirstName());
        result.put("lastName", user.getLastName());
        result.put("birth", user.getBirth());
        result.put("sex", user.getSex());
        result.put("statusCode", 200);
        result.put("responseMessage", "회원가입 성공");

        return result;
    }


    // 로그인
    @PostMapping("/user/login")
    public Map<String, Object> login(@RequestBody UserRequestDto requestDto) throws CloneException {
        User user = userService.login(requestDto);

        Map<String, Object> result = new HashMap<>();
        result.put("token", jwtTokenProvider.createToken(user.getUserId(), user.getUserId())); // "username" : {username}
        result.put("userId", user.getUserId());

        result.put("statusCode", 200);
        result.put("responseMessage", "로그인 성공");

        return result;
    }

    //중복확인
    @PostMapping("/signup/duplicate_id")
    public Map<String, String> duplicateId(@RequestBody UserRequestDto userRequestDto) {
        return userService.duplicateId(userRequestDto);
    }

    // jwt refresh토큰 이 없어서 확인차생성 // 새로고침하면 그냥 자동으로 로그아웃됨
    @GetMapping("/user/info")
    public Map<String, String> loginCheck(@AuthenticationPrincipal UserDetailsImpl userDetails) throws CloneException {
        if (userDetails == null) {
            throw new AuthenticationServiceException("로그인이 필요합니다.");
        }
        Map<String, String> result = new HashMap<>();

        result.put("userId", userDetails.getUser().getUserId());
        result.put("firstName", userDetails.getUser().getFirstName());
        result.put("lastName", userDetails.getUser().getLastName());
        result.put("imageUrl", userDetails.getUser().getImageUrl());
        result.put("responseMessage", "사용자 정보 전달");
        result.put("statusCode", "200");
        return result;
    }

    @GetMapping("/user/list")
    public Map<String, Object> getUserDtoList(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        Map<String, Object> result = new HashMap<>();
        List<UserResponseDto> users = userService.getUserDtoList(userDetails);
        result.put("users", users);
        result.put("responseMessage", "사용자 리스트 전달");
        result.put("statusCode", "200");
        return result;
    }

    @PutMapping("/user/image")
    public Map<String, String> updateUserProfileImage(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody UserImageUrlRequestDto requestDto) throws CloneException {
        if (userDetails == null) {
            throw new AuthenticationServiceException("로그인이 필요합니다.");
        }
        User user = userService.updateUserProfileImage(requestDto.getImageUrl(), userDetails.getUser().getUserId());
        Map<String, String> result = new HashMap<>();

        result.put("imageUrl", user.getImageUrl());
        result.put("userId", user.getUserId());
        result.put("responseMessage", "사용자 이미지 수정 완료");
        result.put("statusCode", "200");
        return result;
    }
}
