package org.example.expert.aop;

import jakarta.servlet.http.HttpServletRequest;

import org.example.expert.domain.comment.controller.CommentAdminController;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CommentAdminController.class)
public class AopTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private HttpServletRequest request;

    @Test
    public void testTrackAdminAspect() throws Exception {
        // Mocking the request
        Mockito.when(request.getAttribute("userId")).thenReturn("testUser");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        // Perform the DELETE request
        mockMvc.perform(delete("/admin/comments/1"))
                .andExpect(status().isOk());

    }
}
