package org.example.expert.domain.manager.service;

import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.manager.dto.request.ManagerSaveRequest;
import org.example.expert.domain.manager.dto.response.ManagerResponse;
import org.example.expert.domain.manager.dto.response.ManagerSaveResponse;
import org.example.expert.domain.manager.entity.Manager;
import org.example.expert.domain.manager.repository.ManagerRepository;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.todo.repository.TodoRepository;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
import org.example.expert.domain.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.ObjectUtils;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ManagerServiceTest {

    @Mock
    private ManagerRepository managerRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private TodoRepository todoRepository;
    @InjectMocks
    private ManagerService managerService;

    @Test
    public void manager_목록_조회_시_Todo가_없다면_IRE_에러를_던진다() {
        // given
        long todoId = 1L;
        given(todoRepository.findById(todoId)).willReturn(Optional.empty());

        // when & then
        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () -> managerService.getManagers(todoId));
        assertEquals("Todo not found", exception.getMessage());
    }

    @Test
    void todo의_user가_null인_경우_예외가_발생한다() {
        // given
        AuthUser authUser = new AuthUser(1L, "a@a.com", UserRole.USER);
        long todoId = 1L;
        long managerUserId = 2L;

        Todo todo = new Todo();
        ReflectionTestUtils.setField(todo, "user", null);

        ManagerSaveRequest managerSaveRequest = new ManagerSaveRequest(managerUserId);

        given(todoRepository.findById(todoId)).willReturn(Optional.of(todo));

        // when & then
        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () ->
                managerService.saveManager(authUser, todoId, managerSaveRequest)
        );

        assertEquals("담당자를 등록하려고 하는 유저가 일정을 만든 유저가 유효하지 않습니다.", exception.getMessage());
    }

    @Test
    @DisplayName("user랑_todo작성자_불일치")
    void test1() {
        // given
        AuthUser authUser = new AuthUser(1L, "a@a.com", UserRole.USER);
        User user1 = User.fromAuthUser(authUser);  // 일정을 만든 유저
        ReflectionTestUtils.setField(user1, "id", 1L);

        User user2 = new User("t2@t.t", "t", UserRole.USER);
        ReflectionTestUtils.setField(user1, "id", 2L);

        long todoId = 1L;
        Todo todo = new Todo("Test Title", "Test Contents", "Sunny", user2);

        long managerUserId = 1L;
        User managerUser = user1;
        ReflectionTestUtils.setField(managerUser, "id", managerUserId);

        ManagerSaveRequest managerSaveRequest = new ManagerSaveRequest(managerUserId); // request dto 생성

        given(todoRepository.findById(todoId)).willReturn(Optional.of(todo));

        // when
        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () -> managerService.saveManager(authUser, todoId, managerSaveRequest));

        //then
        assertEquals("담당자를 등록하려고 하는 유저가 일정을 만든 유저가 유효하지 않습니다.", exception.getMessage());

    }

    @Test
    @DisplayName("saveManager_본인_담당자_등록불가")
    void test2() {
        // given
        AuthUser authUser = new AuthUser(1L, "a@a.com", UserRole.USER);
        User user = User.fromAuthUser(authUser);  // 일정을 만든 유저
        ReflectionTestUtils.setField(user, "id", 1L);

        long todoId = 1L;
        Todo todo = new Todo("Test Title", "Test Contents", "Sunny", user);

        long managerUserId = 1L;
        User managerUser = user;
        ReflectionTestUtils.setField(managerUser, "id", managerUserId);

        ManagerSaveRequest managerSaveRequest = new ManagerSaveRequest(managerUserId); // request dto 생성

        given(todoRepository.findById(todoId)).willReturn(Optional.of(todo));
        given(userRepository.findById(managerUserId)).willReturn(Optional.of(managerUser));


        // when
        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () -> managerService.saveManager(authUser, todoId, managerSaveRequest));

        //then
        assertEquals("일정 작성자는 본인을 담당자로 등록할 수 없습니다.", exception.getMessage());

    }

    @Test // 테스트코드 샘플
    public void manager_목록_조회에_성공한다() {
        // given
        long todoId = 1L;
        User user = new User("user1@example.com", "password", UserRole.USER);
        Todo todo = new Todo("Title", "Contents", "Sunny", user);
        ReflectionTestUtils.setField(todo, "id", todoId);

        Manager mockManager = new Manager(todo.getUser(), todo);
        List<Manager> managerList = List.of(mockManager);

        given(todoRepository.findById(todoId)).willReturn(Optional.of(todo));
        given(managerRepository.findByTodoIdWithUser(todoId)).willReturn(managerList);

        // when
        List<ManagerResponse> managerResponses = managerService.getManagers(todoId);

        // then
        assertEquals(1, managerResponses.size());
        assertEquals(mockManager.getId(), managerResponses.get(0).getId());
        assertEquals(mockManager.getUser().getEmail(), managerResponses.get(0).getUser().getEmail());
    }

    @Test
        // 테스트코드 샘플
    void todo가_정상적으로_등록된다() {
        // given
        AuthUser authUser = new AuthUser(1L, "a@a.com", UserRole.USER);
        User user = User.fromAuthUser(authUser);  // 일정을 만든 유저

        long todoId = 1L;
        Todo todo = new Todo("Test Title", "Test Contents", "Sunny", user);

        long managerUserId = 2L;
        User managerUser = new User("b@b.com", "password", UserRole.USER);  // 매니저로 등록할 유저
        ReflectionTestUtils.setField(managerUser, "id", managerUserId);

        ManagerSaveRequest managerSaveRequest = new ManagerSaveRequest(managerUserId); // request dto 생성

        given(todoRepository.findById(todoId)).willReturn(Optional.of(todo));
        given(userRepository.findById(managerUserId)).willReturn(Optional.of(managerUser));
        given(managerRepository.save(any(Manager.class))).willAnswer(invocation -> invocation.getArgument(0));

        // when
        ManagerSaveResponse response = managerService.saveManager(authUser, todoId, managerSaveRequest);

        // then
        assertNotNull(response);
        assertEquals(managerUser.getId(), response.getUser().getId());
        assertEquals(managerUser.getEmail(), response.getUser().getEmail());
    }

    @Test
    @DisplayName("deleteManger_성공")
    void test3() {
// given
        AuthUser authUser = new AuthUser(1L, "a@a.com", UserRole.USER);
        User user = User.fromAuthUser(authUser);  // 일정을 만든 유저

        long todoId = 1L;
        Todo todo = new Todo("Test Title", "Test Contents", "Sunny", user);
        ReflectionTestUtils.setField(todo, "id", todoId);


        long managerUserId = 2L;
        User managerUser = new User("b@b.com", "password", UserRole.USER);  // 매니저로 등록할 유저
        ReflectionTestUtils.setField(managerUser, "id", managerUserId);

        long managerId = 1L;
        Manager manager = new Manager(user, todo);
        ReflectionTestUtils.setField(manager, "id", managerId);

        given(userRepository.findById(anyLong())).willReturn(Optional.of(user));
        given(todoRepository.findById(anyLong())).willReturn(Optional.of(todo));
        given(managerRepository.findById(anyLong())).willReturn(Optional.of(manager));

        //when
        managerService.deleteManager(authUser, todoId, managerUserId);

        //then
        verify(managerRepository, times(1)).delete(any(Manager.class));
    }

    @Test
    @DisplayName("deleteManger_user_없음")
    void test4() {
        // given
        AuthUser authUser = new AuthUser(1L, "a@a.com", UserRole.USER);
        User user = User.fromAuthUser(authUser);  // 일정을 만든 유저

        long todoId = 1L;
        Todo todo = new Todo("Test Title", "Test Contents", "Sunny", user);

        long managerUserId = 2L;
        User managerUser = new User("b@b.com", "password", UserRole.USER);  // 매니저로 등록할 유저
        ReflectionTestUtils.setField(managerUser, "id", managerUserId);

        given(userRepository.findById(anyLong())).willReturn(Optional.empty());

        //when
        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () -> managerService.deleteManager(authUser, todoId, managerUserId));

        //then
        assertEquals("User not found", exception.getMessage());
    }

    @Test
    @DisplayName("deleteManger_todo_없음")
    void test5() {
        // given
        AuthUser authUser = new AuthUser(1L, "a@a.com", UserRole.USER);
        User user = User.fromAuthUser(authUser);  // 일정을 만든 유저

        long todoId = 1L;
        Todo todo = new Todo("Test Title", "Test Contents", "Sunny", user);

        long managerUserId = 2L;
        User managerUser = new User("b@b.com", "password", UserRole.USER);  // 매니저로 등록할 유저
        ReflectionTestUtils.setField(managerUser, "id", managerUserId);

        given(userRepository.findById(anyLong())).willReturn(Optional.of(user));
        given(todoRepository.findById(anyLong())).willReturn(Optional.empty());

        //when
        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () -> managerService.deleteManager(authUser, todoId, managerUserId));

        //then
        assertEquals("Todo not found", exception.getMessage());
    }

    @Test
    @DisplayName("deleteManger_일정_만든_user_유효하지_않음_null_ver")
    void test6() {
        // given
        AuthUser authUser = new AuthUser(1L, "a@a.com", UserRole.USER);
        User user = User.fromAuthUser(authUser);  // 일정을 만든 유저

        long todoId = 1L;
        Todo todo = new Todo("Test Title", "Test Contents", "Sunny", null);

        long managerUserId = 2L;
        User managerUser = new User("b@b.com", "password", UserRole.USER);  // 매니저로 등록할 유저
        ReflectionTestUtils.setField(managerUser, "id", managerUserId);

        given(userRepository.findById(anyLong())).willReturn(Optional.of(user));
        given(todoRepository.findById(anyLong())).willReturn(Optional.of(todo));


        //when
        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () -> managerService.deleteManager(authUser, todoId, managerUserId));

        //then
        assertEquals("해당 일정을 만든 유저가 유효하지 않습니다.", exception.getMessage());
    }

    @Test
    @DisplayName("nullSafeEquals")
    void test6_1() {
        // given
        AuthUser authUser = new AuthUser(1L, "a@a.com", UserRole.USER);
        User user1 = User.fromAuthUser(authUser);  // 일정을 만든 유저
        ReflectionTestUtils.setField(user1, "id", 1L);

        User user2 = new User("t2@t.t", "t", UserRole.USER);
        ReflectionTestUtils.setField(user1, "id", 2L);

        long todoId = 1L;
        Todo todo = new Todo("Test Title", "Test Contents", "Sunny", user2);

        long managerUserId = 2L;
        User managerUser = new User("b@b.com", "password", UserRole.USER);  // 매니저로 등록할 유저
        ReflectionTestUtils.setField(managerUser, "id", managerUserId);

        given(userRepository.findById(anyLong())).willReturn(Optional.of(user1));
        given(todoRepository.findById(anyLong())).willReturn(Optional.of(todo));


        //when
        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () -> managerService.deleteManager(authUser, todoId, managerUserId));

        //then
        assertEquals("해당 일정을 만든 유저가 유효하지 않습니다.", exception.getMessage());
    }

    @Test
    @DisplayName("deleteManger_매니저_못찾음")
    void test7() {
        // given
        AuthUser authUser = new AuthUser(1L, "a@a.com", UserRole.USER);
        User user = User.fromAuthUser(authUser);  // 일정을 만든 유저

        long todoId = 1L;
        Todo todo = new Todo("Test Title", "Test Contents", "Sunny", user);

        long managerUserId = 2L;
        User managerUser = new User("b@b.com", "password", UserRole.USER);  // 매니저로 등록할 유저
        ReflectionTestUtils.setField(managerUser, "id", managerUserId);

        given(userRepository.findById(anyLong())).willReturn(Optional.of(user));
        given(todoRepository.findById(anyLong())).willReturn(Optional.of(todo));
        given(managerRepository.findById(anyLong())).willReturn(Optional.empty());

        //when
        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () -> managerService.deleteManager(authUser, todoId, managerUserId));

        //then
        assertEquals("Manager not found", exception.getMessage());
    }

    @Test
    @DisplayName("deleteManger_담당자_아님")
    void test8() {
        // given
        AuthUser authUser = new AuthUser(1L, "a@a.com", UserRole.USER);
        User user = User.fromAuthUser(authUser);  // 일정을 만든 유저

        long todoId = 1L;
        Todo todo = new Todo("Test Title", "Test Contents", "Sunny", user);
        ReflectionTestUtils.setField(todo, "id", todoId);
        long todoId2 = 2L;
        Todo todo2 = new Todo("Test Title", "Test Contents", "Sunny", user);
        ReflectionTestUtils.setField(todo2, "id", todoId2);

        long managerUserId = 2L;
        User managerUser = new User("b@b.com", "password", UserRole.USER);  // 매니저로 등록할 유저
        ReflectionTestUtils.setField(managerUser, "id", managerUserId);

        long managerId = 1L;
        Manager manager = new Manager(user, todo2);
        ReflectionTestUtils.setField(manager, "id", managerId);

        given(userRepository.findById(anyLong())).willReturn(Optional.of(user));
        given(todoRepository.findById(anyLong())).willReturn(Optional.of(todo));
        given(managerRepository.findById(anyLong())).willReturn(Optional.of(manager));

        //when
        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () -> managerService.deleteManager(authUser, todoId, managerUserId));

        //then
        assertEquals("해당 일정에 등록된 담당자가 아닙니다.", exception.getMessage());
    }
}
