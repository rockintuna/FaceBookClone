package com.spring.clone.user;

import com.spring.clone.exception.CloneException;
import com.spring.clone.exception.ErrorCode;
import com.spring.clone.sercurity.JwtTokenProvider;
import com.spring.clone.sercurity.UserDetailsImpl;
import com.spring.clone.user.dto.SignUpRequestDto;
import com.spring.clone.user.dto.UserRequestDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class UserController {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    public UserController(UserService userService, JwtTokenProvider jwtTokenProvider){
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
        result.put("sex",user.getSex());
        result.put("statusCode", 200);
        result.put("responseMessage", "회원가입 성공");

        return result;
    }


    // 로그인
    @PostMapping("/user/login")
    public Map<String,Object> login(@RequestBody UserRequestDto requestDto) throws CloneException {
        User user = userService.login(requestDto);

        Map<String,Object> result =new HashMap<>();
        result.put("token", jwtTokenProvider.createToken(user.getUserId(), user.getUserId())); // "username" : {username}
        result.put("userId", user.getUserId());

        result.put("responseMessage", "로그인 성공");
        result.put("statusCode",200);
        return result;
    }

    @PostMapping("/signup/duplicate_id")
    public Map<String, String> duplicateId(@RequestBody UserRequestDto userRequestDto) {
        return userService.duplicateId(userRequestDto);
    }

    // jwt refresh토큰 이 없어서 확인차생성 // 새로고침하면 그냥 자동으로 로그아웃됨
    @GetMapping("/auth")
    public Map<String, String> loginCheck(@AuthenticationPrincipal UserDetailsImpl userDetails) throws CloneException {
        if (userDetails == null) {
            throw new CloneException(ErrorCode.LOGIN_TOKEN_EXPIRE);
        }
        Map<String, String> result = new HashMap<>();
        result.put("userId", userDetails.getUser().getUserId());
        result.put("result", "true");


        return result;
    }
}
