package org.example.expert.domain.todo.service;

import org.example.expert.client.WeatherClient;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.todo.dto.request.TodoSaveRequest;
import org.example.expert.domain.todo.dto.response.TodoResponse;
import org.example.expert.domain.todo.dto.response.TodoSaveResponse;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.todo.repository.TodoRepository;
import org.example.expert.domain.user.dto.response.UserResponse;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;


import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class TodoServiceTest {
    @Mock
    private TodoRepository todoRepository;

    @Mock
    private WeatherClient weatherClient;

    @InjectMocks
    private TodoService todoService;

    @Test
    @DisplayName("saveTodo_성공")
    void test1(){
        AuthUser authUser = new AuthUser(1L, "1@1.1", UserRole.USER);
        User user = User.fromAuthUser(authUser);
        TodoSaveRequest todoSaveRequest = new TodoSaveRequest("1", "1");

        Todo savedTodo = new Todo("1", "1", "1", user);
        ReflectionTestUtils.setField(savedTodo, "id", 1L);  // ID 설정

        given(weatherClient.getTodayWeather()).willReturn("1");
        given(todoRepository.save(any(Todo.class))).willReturn(savedTodo);  // any(Todo.class) 사용


        TodoSaveResponse response = todoService.saveTodo(authUser, todoSaveRequest);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getTitle()).isEqualTo("1");
        assertThat(response.getContents()).isEqualTo("1");
        assertThat(response.getWeather()).isEqualTo("1");
        assertThat(response.getUser().getId()).isEqualTo(1L);
        assertThat(response.getUser().getEmail()).isEqualTo("1@1.1");
    }

    @Test
    @DisplayName("getTodos_(페이징_성공")
    void test2(){

        User user = new User("1@1.1", "1", UserRole.USER);
        ReflectionTestUtils.setField(user, "id", 1L);

        List<Todo> todos = Arrays.asList(
                new Todo("1", "1", "1", user),
                new Todo("2", "2", "2", user)
        );

        ReflectionTestUtils.setField(todos.get(0),"createdAt",LocalDateTime.now());
        ReflectionTestUtils.setField(todos.get(0),"modifiedAt",LocalDateTime.now());
        ReflectionTestUtils.setField(todos.get(1),"createdAt",LocalDateTime.now());
        ReflectionTestUtils.setField(todos.get(1),"modifiedAt",LocalDateTime.now());

        Page<Todo> todoPage = new PageImpl<>(todos);
        Pageable pageable = PageRequest.of(0, 2);

        given(todoRepository.findAllByOrderByModifiedAtDesc(pageable)).willReturn(todoPage);

        Page<TodoResponse> responses = todoService.getTodos(1, 2);

        assertThat(responses.getTotalElements()).isEqualTo(2);// 전체 항목 수
        assertThat(responses.getTotalPages()).isEqualTo(1);//전체 페이지
        assertThat(responses.getContent().size()).isEqualTo(2);
        assertThat(responses.getContent().get(0).getTitle()).isEqualTo("1");
        assertThat(responses.getContent().get(1).getTitle()).isEqualTo("2");




    }

    @Test
    @DisplayName("getTodo_성공")
    void test3(){
        User user = new User("1", "1", UserRole.USER);
        ReflectionTestUtils.setField(user,"id",1L);

        Todo todo = new Todo("1", "1", "1", user);
        ReflectionTestUtils.setField(todo,"id",1L);

        given(todoRepository.findByIdWithUser(anyLong())).willReturn(Optional.of(todo));

        TodoResponse response = todoService.getTodo(anyLong());

        assertThat(response.getId()).isEqualTo(1L);



    }

    @Test
    @DisplayName("getTodo_Todo_부재")
    void test4(){
        given(todoRepository.findByIdWithUser(anyLong())).willReturn(Optional.empty());

        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () -> todoService.getTodo(anyLong()));

        assertEquals("Todo not found", exception.getMessage());
    }
}