package org.example.expert.domain.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.expert.config.AuthUserArgumentResolver;
import org.example.expert.domain.user.dto.request.UserRoleChangeRequest;
import org.example.expert.domain.user.service.UserAdminService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserAdminController.class)
public class UserAdminControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Mock
    private AuthUserArgumentResolver authUserArgumentResolver;

    @MockBean
    private UserAdminService userAdminService;

    @Autowired
    private UserAdminController userAdminController;

    @BeforeEach
    void init(){
        mockMvc = MockMvcBuilders.standaloneSetup(userAdminController)
                .setCustomArgumentResolvers(authUserArgumentResolver).build();
    }

    @Test
    @DisplayName("유저역할바꾸긔컨트롤러")
    void 유역바()throws Exception{
        UserRoleChangeRequest userRoleChangeRequest = new UserRoleChangeRequest("USER");

        ResultActions resultActions = mockMvc.perform(patch("/admin/users/{userId}",1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(userRoleChangeRequest)));

        resultActions.andExpect(status().isOk());
    }

}
