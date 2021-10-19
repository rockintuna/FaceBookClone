package com.spring.clone.user;

import com.spring.clone.exception.CloneException;
import com.spring.clone.exception.ErrorCode;
import com.spring.clone.user.dto.SignUpRequestDto;
import com.spring.clone.user.dto.UserRequestDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class UserService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;


    private static final String ADMIN_TOKEN = "AAABnv/xRVklrnYxKZ0aHgTBcXukeZygoC";
    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }




    public User registerUser(SignUpRequestDto requestDto) throws CloneException {

        // 패스워드 암호화
        String pwd = passwordEncoder.encode(requestDto.getPwd());

        //가입 아이디 중복체크
        String userId = requestDto.getUserId();
        Optional<User> found = userRepository.findByUserId(userId);
        if (found.isPresent()){
            throw new CloneException(ErrorCode.EMAIL_DUPLICATE);
        }


        //비밀번호확인
        String password = requestDto.getPwd();

        if (!password.isEmpty()) {
            if (!(password.length() >= 6 && password.length() <= 20)) {
                throw new CloneException(ErrorCode.PASSWORD_PATTERN_LENGTH);
            }
        } else {
            throw new CloneException(ErrorCode.PASSWORD_ENTER);
        }


        //회원정보저장
        requestDto.setPwd(pwd);
        User user = new User(requestDto);

        return userRepository.save(user);



    }



    //로그인
    public User login(UserRequestDto requestDto) throws CloneException {
        User user = userRepository.findByUserId(requestDto.getUserId()).orElseThrow(
                () -> new CloneException(ErrorCode.USER_NOT_FOUND)
        );


        if (!passwordEncoder.matches(requestDto.getPwd(), user.getPwd())) {
            throw new CloneException(ErrorCode.USER_NOT_FOUND);
        }

        return user;
    }

    // 로그인 아이디 중복
    public Map<String, String> duplicateId(UserRequestDto userRequestDto) {
        User user = userRepository.findByUserId(userRequestDto.getUserId()).orElse(null);

        Map<String, String> result = new HashMap<>();
        if (user == null) {
            result.put("result", "true");
            return result;
        }

        result.put("result", "fail");
        result.put("message", "중복된 아이디가 존재합니다.");
        return result;
    }
}

