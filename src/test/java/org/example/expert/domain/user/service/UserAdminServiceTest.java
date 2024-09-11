package org.example.expert.domain.user.service;

import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.user.dto.request.UserRoleChangeRequest;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
import org.example.expert.domain.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;


@ExtendWith(MockitoExtension.class)
class UserAdminServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserAdminService userAdminService;

    @Test
    @DisplayName("changeUserRole_성공")
    void test1(){
        UserRoleChangeRequest userRoleChangeRequest = new UserRoleChangeRequest("ADMIN");
        User user = new User("1","1", UserRole.USER);

        given(userRepository.findById(anyLong())).willReturn(Optional.of(user));

        userAdminService.changeUserRole(anyLong(),userRoleChangeRequest);

        assertEquals(user.getUserRole(),UserRole.ADMIN);

    }

    @Test
    @DisplayName("changeUserRole_실패")
    void test2(){
        UserRoleChangeRequest userRoleChangeRequest = new UserRoleChangeRequest("ADMIN");
        User user = new User("1","1", UserRole.USER);

        given(userRepository.findById(anyLong())).willReturn(Optional.empty());

        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () -> userAdminService.changeUserRole(anyLong(), userRoleChangeRequest));

        assertEquals("User not found", exception.getMessage());

    }

}