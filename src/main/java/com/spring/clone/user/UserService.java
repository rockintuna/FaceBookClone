package com.spring.clone.user;

import com.spring.clone.exception.CloneException;
import com.spring.clone.exception.ErrorCode;
import com.spring.clone.sercurity.UserDetailsImpl;
import com.spring.clone.user.dto.SignUpRequestDto;
import com.spring.clone.user.dto.UserRequestDto;
import com.spring.clone.user.dto.UserResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        if (requestDto.getUserId() == "") {
            throw  new CloneException(ErrorCode.REGISTER_ERROR);
        }
        if (requestDto.getPwd() == "") {
            throw  new CloneException(ErrorCode.REGISTER_ERROR);
        }
        if (requestDto.getLastName() == "") {
            throw  new CloneException(ErrorCode.REGISTER_ERROR);
        }
        if (requestDto.getBirth() == null) {
            throw  new CloneException(ErrorCode.REGISTER_ERROR);
        }
        if (requestDto.getFirstName() == "") {
            throw  new CloneException(ErrorCode.REGISTER_ERROR);
        }
        //비밀번호확인
        String password = requestDto.getPwd();
        String passwordCheck = requestDto.getPwdCheck();

        if (!password.isEmpty() && !passwordCheck.isEmpty()) {
            if (password.length() >= 6 && password.length() <= 20) {
                if (!password.equals(passwordCheck)) {
                    throw new CloneException(ErrorCode.PASSWORD_EQUAL);
                }
            } else {
                throw new CloneException(ErrorCode.PASSWORD_PATTERN_LENGTH);

            }
        } else {
            throw new CloneException(ErrorCode.PASSWORD_ENTER);
        }





        //가입 아이디 중복체크
        String userId = requestDto.getUserId();
        if (!isValidEmail(userId)) {
            throw new CloneException(ErrorCode.EMAIL_FORM_INVALID);
        }
        Optional<User> found = userRepository.findByUserId(userId);
        if (found.isPresent()){
            throw new CloneException(ErrorCode.EMAIL_DUPLICATE);
        }

        //이름확인
        String firstName = requestDto.getFirstName();
        if (firstName.isEmpty()) {
            throw new CloneException(ErrorCode.FIRSTNAME_ENTER);
        }

        // 패스워드 암호화
        String pwd = passwordEncoder.encode(requestDto.getPwd());


        //회원정보저장
        requestDto.setPwd(pwd);
        User user = new User(requestDto);

        return userRepository.save(user);



    }


    private boolean isValidEmail(String email) {
        boolean err = false;
        String regex = "^[_a-z0-9-]+(.[_a-z0-9-]+)*@(?:\\w+\\.)+\\w+$";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(email);
        if(m.matches()) { err = true; } return err;
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

    public User updateUserProfileImage(String imageUrl, String userId) {
        User user = userRepository.findByUserId(userId).orElseThrow(
                () -> new CloneException(ErrorCode.USER_NOT_FOUND)
        );
        user.setImageUrl(imageUrl);
        return userRepository.save(user);
    }

    public List<UserResponseDto> getUserDtoList(UserDetailsImpl userDetails) {
        List<User> users = userRepository.findAll();
        return UserResponseDto.listOf(users, userDetails);
    }
}

