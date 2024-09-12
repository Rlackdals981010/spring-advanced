package org.example.expert.aop;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import org.aspectj.lang.JoinPoint;
import org.example.expert.aop.AspectModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;

@ExtendWith(MockitoExtension.class)
@SpringJUnitConfig
public class AspectModuleTest {

    @InjectMocks
    private AspectModule aspectModule;

    @Mock
    private HttpServletRequest request;

    @BeforeEach
    void setUp() {
        // Mock HttpServletRequest
        when(request.getRequestURL()).thenReturn(new StringBuffer("http://localhost:8080/admin/comments/1"));

        ServletRequestAttributes attributes = new ServletRequestAttributes(request);
        RequestContextHolder.setRequestAttributes(attributes);
    }

    @Test
    @DisplayName("AspectModule의 afterTrackAdmin 메서드 호출 검증")
    void afterTrackAdmin_검증() throws Exception {
        JoinPoint joinPoint = mock(JoinPoint.class);

        // AOP 메서드에서 호출될 인자 설정
        when(joinPoint.getArgs()).thenReturn(new Object[]{1L});

        // AspectModule의 afterTrackAdmin 메서드 호출
        aspectModule.afterTrackAdmin(joinPoint);

        // 검증 로직 추가 (여기서는 검증을 생략합니다)
    }
}
