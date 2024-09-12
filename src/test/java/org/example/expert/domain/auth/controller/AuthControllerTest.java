package org.example.expert.domain.auth.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.expert.domain.auth.dto.request.SigninRequest;
import org.example.expert.domain.auth.dto.request.SignupRequest;
import org.example.expert.domain.auth.dto.response.SigninResponse;
import org.example.expert.domain.auth.dto.response.SignupResponse;
import org.example.expert.domain.auth.service.AuthService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;


import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;


    @Test
    @DisplayName("post_/auth/signup")
    void 회원가입() throws Exception {

        SignupRequest signupRequest = new SignupRequest("t@t.t", "1", "ADMIN");
        given(authService.signup(signupRequest)).willReturn(new SignupResponse("bearerToken"));

        ResultActions resultActions = mockMvc.perform(post("/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(signupRequest)));

        resultActions.andExpect(status().isOk());

    }

    @Test
    @DisplayName("post_/auth/signin")
    void 로그인() throws Exception {

        SigninRequest signinRequest = new SigninRequest("t@t.t","1");
        given(authService.signin(signinRequest)).willReturn(new SigninResponse("bearerToken"));

        ResultActions resultActions = mockMvc.perform(post("/auth/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(signinRequest)));

        resultActions.andExpect(status().isOk());
    }

}
