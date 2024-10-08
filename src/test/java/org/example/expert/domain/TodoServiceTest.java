package org.example.expert.domain;


import org.example.expert.client.WeatherClient;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.todo.dto.response.TodoResponse;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.todo.repository.TodoRepository;
import org.example.expert.domain.todo.service.TodoService;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class) // 목업 쓰려고
public class TodoServiceTest {

    @Mock
    private TodoRepository todoRepository;
    @Mock
    private WeatherClient weatherClient;

    @InjectMocks
    private TodoService todoService;


    @Test
    public void 일정_조회_정상동작(){
        //given
        long todoId = 1L;
        long userId= 2L;
        User user = new User("email", "pwd", UserRole.USER);
        ReflectionTestUtils.setField(user,"id",userId); //userid 세팅
        Todo todo = new Todo("title", "contents", "weather", user);
        ReflectionTestUtils.setField(todo,"id",todoId);

        given(todoRepository.findByIdWithUser(anyLong())).willReturn(Optional.of(todo));
        //when
        TodoResponse todoResponse =todoService.getTodo(todoId);

        //then
        assertNotNull(todoResponse);
    }

    @Test
    public void 일정_조회_entity_없음(){
        //given
        long todoId = 1L;
        long userId= 2L;
        User user = new User("email", "pwd", UserRole.USER);
        ReflectionTestUtils.setField(user,"id",userId); //userid 세팅
        Todo todo = new Todo("title", "contents", "weather", user);
        ReflectionTestUtils.setField(todo,"id",todoId);

        given(todoRepository.findByIdWithUser(anyLong())).willReturn(Optional.empty());

        //when
        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () -> todoService.getTodo(todoId));

        //then
        assertEquals("Todo not found",exception.getMessage());

    }

}
