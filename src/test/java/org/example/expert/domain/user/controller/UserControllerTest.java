package org.example.expert.domain.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.expert.config.AuthUserArgumentResolver;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.user.dto.request.UserChangePasswordRequest;
import org.example.expert.domain.user.dto.response.UserResponse;
import org.example.expert.domain.user.enums.UserRole;
import org.example.expert.domain.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private AuthUserArgumentResolver authUserArgumentResolver;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserController userController;

    private String token;
    private AuthUser authUser;


    @BeforeEach
    void init() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController)
                .setCustomArgumentResolvers(authUserArgumentResolver).build();

        this.token = "Bearer test-token";  // 임의의 테스트 토큰 설정
        this.authUser = new AuthUser(1L,"test@test.com", UserRole.USER);
    }

    @Test
    @DisplayName("get_/users/{userId}")
    void 유저조회() throws Exception {
        given(userService.getUser(anyLong())).willReturn(new UserResponse(1L, "1"));

        ResultActions resultActions = mockMvc.perform(get("/users/{userId}", 1L));

        resultActions.andExpect(status().isOk());
    }

    @Test
    @DisplayName("put_/users")
    void 비번바꾸기() throws Exception {
        // given
        UserChangePasswordRequest requestDto = new UserChangePasswordRequest("1234", "2345");

        String postInfo = objectMapper.writeValueAsString(requestDto);

        // AuthUserArgumentResolver와 UserService의 동작을 Mock 처리
        given(authUserArgumentResolver.supportsParameter(any())).willReturn(true);
        given(authUserArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(authUser);
        doNothing().when(userService).changePassword(anyLong(), any());

        // when
        ResultActions resultActions = mockMvc.perform(put("/users")
                .header(HttpHeaders.AUTHORIZATION, token)
                .content(postInfo)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        );

        // then
        resultActions.andExpect(status().isOk()).andDo(print());

        // 서비스 메소드가 정확히 한 번 호출되었는지 검증
        verify(userService, times(1)).changePassword(anyLong(), any());


    }

}
