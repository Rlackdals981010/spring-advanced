package org.example.expert.domain.auth.service;

import org.aspectj.lang.annotation.Before;
import org.example.expert.config.JwtUtil;
import org.example.expert.config.PasswordEncoder;
import org.example.expert.domain.auth.dto.request.SigninRequest;
import org.example.expert.domain.auth.dto.request.SignupRequest;
import org.example.expert.domain.auth.dto.response.SigninResponse;
import org.example.expert.domain.auth.dto.response.SignupResponse;
import org.example.expert.domain.auth.exception.AuthException;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
import org.example.expert.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class) // Mock 객체를 위함
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtUtil jwtUtil;
    @InjectMocks
    private AuthService authService;

    @Test
    @DisplayName("가입_성공_테스트")
    void test1(){
        //Given
        String email = "test@test.test";
        String password = "Test1!";
        String userRole = "USER";

        SignupRequest signupRequest = new SignupRequest(email, password, userRole);

        UserRole role = UserRole.of(userRole);
        String encodedPassword = "encodedPassword";
        User newUser = new User(email, encodedPassword, role);
        User savedUser = new User(email, encodedPassword, role);
        String bearerToken = "generatedToken";

        given(userRepository.existsByEmail(email)).willReturn(false);
        given(passwordEncoder.encode(password)).willReturn(encodedPassword);
        given(userRepository.save(any(User.class))).willReturn(savedUser);
        given(jwtUtil.createToken(savedUser.getId(), savedUser.getEmail(), role)).willReturn(bearerToken);

        //When
        SignupResponse response = authService.signup(signupRequest);

        //Then
        assertThat(response).isNotNull();
        assertThat(response.getBearerToken()).isEqualTo(bearerToken);
    }

    @Test
    @DisplayName("가입_실패_이메일 중복_테스트")
    void test2(){
        //Given
        String email = "test@test.test";
        String password = "Test1!";
        String userRole = "USER";

        SignupRequest signupRequest = new SignupRequest(email, password, userRole);


        when(userRepository.existsByEmail(email)).thenReturn(true); // 이미 가입한 메일이 있다고 반환

        //When
        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () -> authService.signup(signupRequest));

        //Then
        assertEquals("이미 존재하는 이메일입니다.", exception.getMessage());
    }

    @Test
    @DisplayName("로그인_성공_테스트")
    void test3(){
        //Given
        String email = "test@test.test";
        String password = "Test1!";
        String encodedPassword = "encodedPassword";
        String bearerToken = "generatedToken";

        User user = new User(email, encodedPassword, UserRole.USER);

        SigninRequest signinRequest = new SigninRequest(email, password);

        given(userRepository.findByEmail(email)).willReturn(Optional.of(user)); // 가입된 메일
        given(passwordEncoder.matches(password, encodedPassword)).willReturn(true);
        given(jwtUtil.createToken(user.getId(), user.getEmail(), user.getUserRole())).willReturn(bearerToken);
        //When
        SigninResponse response =authService.signin(signinRequest);

        //Then
        assertThat(response).isNotNull();
        assertThat(response.getBearerToken()).isEqualTo(bearerToken);
    }

    @Test
    @DisplayName("로그인_실패_미가입_유저_테스트")
    void test4(){
        //Given
        String email = "test@test.test";
        String password = "Test1!";
        SigninRequest signinRequest = new SigninRequest(email, password);
        given(userRepository.findByEmail(email)).willReturn(Optional.empty());

        //When
        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () -> {
            authService.signin(signinRequest);
        });
        //Then
        assertEquals("가입되지 않은 유저입니다.", exception.getMessage());
    }

    @Test
    @DisplayName("로그인_실패_비밀번호_오류_테스트")
    void test5(){
        //Given
        String email = "test@test.test";
        String password = "Test1!";
        String encodedPassword = "encodedPassword";
        String bearerToken = "generatedToken";

        User user = new User(email, encodedPassword, UserRole.USER);

        SigninRequest signinRequest = new SigninRequest(email, password);

        given(userRepository.findByEmail(email)).willReturn(Optional.of(user)); // 가입된 메일
        AuthException exception =assertThrows(AuthException.class, () -> authService.signin(signinRequest));

        //when
        assertEquals("잘못된 비밀번호입니다.", exception.getMessage());

    }

}