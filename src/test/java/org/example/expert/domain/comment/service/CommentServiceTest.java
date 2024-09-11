package org.example.expert.domain.comment.service;

import org.example.expert.domain.comment.dto.request.CommentSaveRequest;
import org.example.expert.domain.comment.dto.response.CommentResponse;
import org.example.expert.domain.comment.dto.response.CommentSaveResponse;
import org.example.expert.domain.comment.entity.Comment;
import org.example.expert.domain.comment.repository.CommentRepository;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.common.exception.InvalidRequestException;

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
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;
    @Mock
    private TodoRepository todoRepository;
    @InjectMocks
    private CommentService commentService;

    @Test
    public void comment_등록_중_할일을_찾지_못해_에러가_발생한다() {
        // given
        long todoId = 1;
        CommentSaveRequest request = new CommentSaveRequest("contents");
        AuthUser authUser = new AuthUser(1L, "email", UserRole.USER);

        given(todoRepository.findById(anyLong())).willReturn(Optional.empty());

        // when
        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () -> {
            commentService.saveComment(authUser, todoId, request);
        });

        // then
        assertEquals("Todo not found", exception.getMessage());
    }

    @Test
    public void comment를_정상적으로_등록한다() {
        // given
        long todoId = 1;
        CommentSaveRequest request = new CommentSaveRequest("contents");
        AuthUser authUser = new AuthUser(1L, "email", UserRole.USER);
        User user = User.fromAuthUser(authUser);
        Todo todo = new Todo("title", "title", "contents", user);
        Comment comment = new Comment(request.getContents(), user, todo);

        given(todoRepository.findById(anyLong())).willReturn(Optional.of(todo));
        given(commentRepository.save(any())).willReturn(comment);

        // when
        CommentSaveResponse result = commentService.saveComment(authUser, todoId, request);

        // then
        assertNotNull(result);
    }

    @Test
    @DisplayName("getComments 정상 동작")
    void test3(){
        // given
        long todoId = 1L;
        User user = new User("t@t.t", "t", UserRole.USER);
        ReflectionTestUtils.setField(user, "id", 1L);
        Todo todo = new Todo("t", "t", "t", user);
        ReflectionTestUtils.setField(todo, "id", 1L);

        // 댓글 생성
        Comment comment1 = new Comment("1", user, todo);
        Comment comment2 = new Comment("2", user, todo);

        // Mock된 레포지토리 응답
        List<Comment> commentList = List.of(comment1, comment2);

        // 예상되는 응답
        UserResponse ur = new UserResponse(user.getId(), user.getEmail());
        CommentResponse c1 = new CommentResponse(comment1.getId(), "1", ur);
        CommentResponse c2 = new CommentResponse(comment2.getId(), "2", ur);
        List<CommentResponse> commentResponseList = List.of(c1, c2);

        given(commentRepository.findByTodoIdWithUser(todoId)).willReturn(commentList);

        // when
        List<CommentResponse> responses = commentService.getComments(todoId);

        // then. 리스트 반환시 주소로 찍혀서 내용물 하나하나 했음. 아니면 다른 방법 찾아봐야할듯
        for (int i = 0; i < commentResponseList.size(); i++) {
            CommentResponse expected = commentResponseList.get(i);
            CommentResponse actual = responses.get(i);

            assertThat(actual.getId()).isEqualTo(expected.getId());
            assertThat(actual.getContents()).isEqualTo(expected.getContents());
            assertThat(actual.getUser().getId()).isEqualTo(expected.getUser().getId());
            assertThat(actual.getUser().getEmail()).isEqualTo(expected.getUser().getEmail());
        }
    }
}
