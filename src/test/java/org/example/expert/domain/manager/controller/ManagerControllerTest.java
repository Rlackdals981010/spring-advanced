package org.example.expert.domain.manager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.expert.config.AuthUserArgumentResolver;
import org.example.expert.config.JwtUtil;
import org.example.expert.domain.common.annotation.Auth;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.manager.dto.request.ManagerSaveRequest;
import org.example.expert.domain.manager.dto.response.ManagerSaveResponse;
import org.example.expert.domain.manager.service.ManagerService;
import org.example.expert.domain.user.dto.response.UserResponse;
import org.example.expert.domain.user.enums.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.convert.DataSizeUnit;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ManagerController.class)
public class ManagerControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ManagerService managerService;
    @MockBean
    private JwtUtil jwtUtil;
    @Mock
    private AuthUserArgumentResolver authUserArgumentResolver;

    @Autowired
    private ManagerController managerController;


    @BeforeEach
    public void init() {
        mockMvc = MockMvcBuilders.standaloneSetup(managerController) // 컨트롤러만 테스트하겠음
                .setCustomArgumentResolvers(authUserArgumentResolver).build(); // @Auth 처리
    }

    @Test
    @DisplayName("post_todos/{todoId}/managers")
    void 매니저_저장() throws Exception {

        ManagerSaveRequest managerSaveRequest = new ManagerSaveRequest(1L);
        given(managerService.saveManager(any(AuthUser.class), anyLong(), any(ManagerSaveRequest.class))).willReturn(new ManagerSaveResponse(1L, new UserResponse(1L, "t@t.t")));

        // MockMvc 요청 실행
        ResultActions resultActions = mockMvc.perform(post("/todos/{todoId}/managers", 1L)
                .content(new ObjectMapper().writeValueAsString(managerSaveRequest))
                .contentType(MediaType.APPLICATION_JSON));

        // 응답 검증
        resultActions.andExpect(status().isOk());
    }

    @Test
    @DisplayName("get_/todos/{todoId}/managers")
    void 맴버조회()throws Exception{

        given(managerService.getManagers(anyLong())).willReturn(List.of());

        ResultActions resultActions = mockMvc.perform(get("/todos/{todoId}/managers",1L));

        resultActions.andExpect(status().isOk());
    }

    @Test
    @DisplayName("delete_/todos/{todoId}/managers/{managerId}")
    void 매니저지우기()throws Exception{
        ResultActions resultActions = mockMvc.perform(delete("/todos/{todoId}/managers/{managerId}", 1L,1L));
        resultActions.andExpect(status().isOk());
    }
}
