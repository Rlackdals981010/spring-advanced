package org.example.expert.domain.todo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.expert.config.AuthUserArgumentResolver;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.todo.dto.request.TodoSaveRequest;
import org.example.expert.domain.todo.dto.response.TodoResponse;
import org.example.expert.domain.todo.dto.response.TodoSaveResponse;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.todo.service.TodoService;
import org.example.expert.domain.user.dto.response.UserResponse;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TodoController.class)
public class TodoControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private TodoController todoController;

    @Mock
    private AuthUserArgumentResolver authUserArgumentResolver;

    @MockBean
    private TodoService todoService;

    @BeforeEach
    public void init(){
        mockMvc = MockMvcBuilders.standaloneSetup(todoController)
                .setCustomArgumentResolvers(authUserArgumentResolver).build();
    }

    @Test
    @DisplayName("post_/todos")
    void Todo_저장() throws Exception {
        TodoSaveRequest todoSaveRequest = new TodoSaveRequest("1", "1");
        given(todoService.saveTodo(any(AuthUser.class), any(TodoSaveRequest.class)))
                .willReturn(new TodoSaveResponse(1L, "1", "1", "1", new UserResponse(1L, "1")));

        ResultActions resultActions = mockMvc.perform(post("/todos")
                .content(new ObjectMapper().writeValueAsString(todoSaveRequest))
                .contentType(MediaType.APPLICATION_JSON));

        resultActions.andExpect(status().isOk());
    }

    @Test
    @DisplayName("get_/todos")
    void todo_페이징()throws Exception{

        Pageable pageable = PageRequest.of(0, 10);
        Page<TodoResponse> todoPage = new PageImpl<>(Collections.emptyList(), pageable, 0);

        given(todoService.getTodos(anyInt(), anyInt())).willReturn(todoPage);

        ResultActions resultActions = mockMvc.perform(get("/todos"));

        resultActions.andExpect(status().isOk());
    }

    @Test
    @DisplayName("get_/todos/{todoId}")
    void todo_단건조회()throws Exception{
        given(todoService.getTodo(anyLong()))
                .willReturn(new TodoResponse(1L, "1", "1", "1", new UserResponse(1L, "1"), LocalDateTime.now(), LocalDateTime.now()));

        ResultActions resultActions = mockMvc.perform(get("/todos/{todoId}",1L));

        resultActions.andExpect(status().isOk());

    }



}
