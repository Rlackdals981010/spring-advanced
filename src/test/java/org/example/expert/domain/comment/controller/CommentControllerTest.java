package org.example.expert.domain.comment.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.expert.config.AuthUserArgumentResolver;
import org.example.expert.domain.comment.dto.request.CommentSaveRequest;
import org.example.expert.domain.comment.dto.response.CommentResponse;
import org.example.expert.domain.comment.dto.response.CommentSaveResponse;
import org.example.expert.domain.comment.service.CommentService;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.user.dto.response.UserResponse;
import org.example.expert.domain.user.enums.UserRole;
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

import java.util.List;
import java.util.Optional;

import static org.example.expert.domain.user.enums.UserRole.ADMIN;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CommentController.class)
public class CommentControllerTest {

    @MockBean
    private CommentService commentService;


    @Mock
    private AuthUserArgumentResolver authUserArgumentResolver;

    @Autowired
    private CommentController commentController;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    public void init(){
        mockMvc = MockMvcBuilders.standaloneSetup(commentController)
                .setCustomArgumentResolvers(authUserArgumentResolver).build();
    }

    @Test
    @DisplayName("post_/todos/{todoId}/comments")
    void 댓글저장() throws Exception {
        AuthUser authUser = new AuthUser(1L, "t@.t", ADMIN);
        CommentSaveRequest commentSaveRequest = new CommentSaveRequest("1");

        given(commentService.saveComment(any(AuthUser.class), eq(1L), any(CommentSaveRequest.class)))
                .willReturn(new CommentSaveResponse(1L, "1", new UserResponse(1L, "t@t.t")));

        ResultActions resultActions = mockMvc.perform(post("/todos/{todoId}/comments", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(commentSaveRequest))
                .header("Authorization", "Bearer token"));

        resultActions.andExpect(status().isOk());

    }

    @Test
    @DisplayName("get_/todos/{todoId}/comments")
    void 댓글들_조회()throws Exception{

        given(commentService.getComments(anyLong())).willReturn(List.of());

        ResultActions resultActions = mockMvc.perform(get("/todos/{todoId}/comments", 1L));

        resultActions.andExpect(status().isOk());
    }


}
