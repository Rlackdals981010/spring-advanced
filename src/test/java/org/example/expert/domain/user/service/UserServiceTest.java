package org.example.expert.domain.user.service;

import org.example.expert.config.PasswordEncoder;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.user.dto.request.UserChangePasswordRequest;
import org.example.expert.domain.user.dto.response.UserResponse;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
import org.example.expert.domain.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;


import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("getUser_성공")
    void test1(){
        User user = new User("1", "1", UserRole.USER);
        ReflectionTestUtils.setField(user,"id",1L);

        given(userRepository.findById(anyLong())).willReturn(Optional.of(user));

        UserResponse response = userService.getUser(anyLong());

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getEmail()).isEqualTo("1");
    }

    @Test
    @DisplayName("getUser_User_없음")
    void test2(){
        given(userRepository.findById(anyLong())).willReturn(Optional.empty());

        InvalidRequestException exception = assertThrows(InvalidRequestException.class,()->userService.getUser(anyLong()));

        assertEquals("User not found",exception.getMessage());
    }


    @Test
    @DisplayName("changePassword_성공")
    void test3(){
        UserChangePasswordRequest userChangePasswordRequest = new UserChangePasswordRequest("1", "11111111A");
        User user = new User("1", "1", UserRole.USER);

        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(passwordEncoder.matches("1", "1")).willReturn((true));
        given(passwordEncoder.matches("11111111A", "1")).willReturn((false));
        given(passwordEncoder.encode("11111111A")).willReturn(("encodedNewPassword"));


        userService.changePassword(1L, userChangePasswordRequest);

        assertEquals("encodedNewPassword", user.getPassword());
    }

    @Test
    @DisplayName("changePassword_실패_checkPassword_실패(짧)")
    void test4(){
        UserChangePasswordRequest userChangePasswordRequest = new UserChangePasswordRequest("1", "1");

        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () -> userService.changePassword(1L, userChangePasswordRequest));

        assertEquals("새 비밀번호는 8자 이상이어야 하고, 숫자와 대문자를 포함해야 합니다.", exception.getMessage());
    }
    @Test
    @DisplayName("changePassword_실패_checkPassword_실패(숫자없)")
    void test4_1(){
        UserChangePasswordRequest userChangePasswordRequest = new UserChangePasswordRequest("1", "Aaaaaaaaa");

        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () -> userService.changePassword(1L, userChangePasswordRequest));

        assertEquals("새 비밀번호는 8자 이상이어야 하고, 숫자와 대문자를 포함해야 합니다.", exception.getMessage());
    }
    @Test
    @DisplayName("changePassword_실패_checkPassword_실패(대문자없)")
    void test4_2(){
        UserChangePasswordRequest userChangePasswordRequest = new UserChangePasswordRequest("1", "111111111");

        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () -> userService.changePassword(1L, userChangePasswordRequest));

        assertEquals("새 비밀번호는 8자 이상이어야 하고, 숫자와 대문자를 포함해야 합니다.", exception.getMessage());
    }

    @Test
    @DisplayName("changePassword_실패_user_없음")
    void test5(){
        UserChangePasswordRequest userChangePasswordRequest = new UserChangePasswordRequest("1", "11111111A");

        User user = new User("1", "1", UserRole.USER);
        ReflectionTestUtils.setField(user,"id",1L);
        given(userRepository.findById(anyLong())).willReturn(Optional.empty());

        InvalidRequestException exception = assertThrows(InvalidRequestException.class,()->userService.changePassword(anyLong(),userChangePasswordRequest));

        assertEquals("User not found", exception.getMessage());
    }

    @Test
    @DisplayName("changePassword_실패_새_비밀번호_기존_동일")
    void test6(){
        UserChangePasswordRequest userChangePasswordRequest = new UserChangePasswordRequest("1", "11111111A");

        User user = new User("1", "1", UserRole.USER);
        ReflectionTestUtils.setField(user,"id",1L);
        given(userRepository.findById(anyLong())).willReturn(Optional.of(user));
        given(passwordEncoder.matches(anyString(), anyString())).willReturn(true);

        InvalidRequestException exception = assertThrows(InvalidRequestException.class,()->userService.changePassword(anyLong(),userChangePasswordRequest));

        assertEquals("새 비밀번호는 기존 비밀번호와 같을 수 없습니다.", exception.getMessage());

    }

    @Test
    @DisplayName("changePassword_실패_기존_비밀번호_틀림")
    void test7(){
        UserChangePasswordRequest userChangePasswordRequest = new UserChangePasswordRequest("1", "11111111A");

        User user = new User("1", "1", UserRole.USER);
        ReflectionTestUtils.setField(user,"id",1L);
        given(userRepository.findById(anyLong())).willReturn(Optional.of(user));
        given(passwordEncoder.matches(anyString(), anyString())).willReturn(false);

        InvalidRequestException exception = assertThrows(InvalidRequestException.class,()->userService.changePassword(anyLong(),userChangePasswordRequest));

        assertEquals("잘못된 비밀번호입니다.", exception.getMessage());

    }
}