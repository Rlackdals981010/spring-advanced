package org.example.expert.aop;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(OutputCaptureExtension.class)
public class AspectModuleTest {

    @Test
    public void testAfterTrackAdmin_logsCorrectly(CapturedOutput output) {
        // Mock HttpServletRequest
        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        mockRequest.setRequestURI("/api/admin");
        mockRequest.setAttribute("userId", 123L);

        // Mock RequestContextHolder
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(mockRequest));

        // AOP 로직을 실행
        AspectModule aspectModule = new AspectModule();
        aspectModule.afterTrackAdmin();

        // 로그 메시지 검증
        String logs = output.getAll();
        assertTrue(logs.contains("요청한 사용자의 ID : 123"));
        assertTrue(logs.contains("API 요청 URL : http://localhost/api/admin"));
    }
}
