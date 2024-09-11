package org.example.expert.aop;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Slf4j
@Aspect
public class AspectModule {

    //포인트 컷
    @Pointcut("@annotation(org.example.expert.aop.trackAdmin)")
    private void trackAdminAnnotation(){}

    @After("trackAdminAnnotation()")
    public void afterTrackAdmin(JoinPoint joinPoint){

        // User id
        Object[] args = joinPoint.getArgs();
        Long userId = null;
        for (Object arg : args) {
            if (arg instanceof Long) {
                userId = (Long) arg;
                break;
            }
        }

        // API 요청 시각
        long start = System.currentTimeMillis();

        // API 요청 URL
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String requestUrl = request.getRequestURL().toString();

        log.info("::: 요청한 사용자의 ID :{} :::" , userId);
        log.info("::: API 요청 시각 : {} :::", start);
        log.info("::: API 요청 URL : {} :::", requestUrl);
    }


//    @Pointcut("execution(* org.example.expert.domain.comment.controller.CommentAdminController.deleteComment(*))")
//    private void commentAdminController() {}
//
//    @Pointcut("execution(* org.example.expert.domain.user.controller.UserAdminController.changeUserRole(..))")
//    private void userAdminController() {}
//
//    //어드바이스
//    @Before("commentAdminController()||userAdminController()")
//    public void saveLog(JoinPoint joinPoint){
//        // 요청 URL 가져오기
//        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
//        String requestUrl = request.getRequestURL().toString();
//
//        // 사용자의 ID는 파라미터로 전달받는다고 가정
//        Object[] args = joinPoint.getArgs();
//        Long userId = null;
//        for (Object arg : args) {
//            if (arg instanceof Long) {
//                userId = (Long) arg; // 예시: Long 타입의 ID를 찾는 경우
//                break;
//            }
//        }
//
//        log.info("::: 요청한 사용자의 ID :{} :::" , userId);
//        log.info("::: API 요청 시각 : {} :::", System.currentTimeMillis());
//        log.info("::: API 요청 URL : {} :::", requestUrl);
//    }

}
