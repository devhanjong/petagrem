package com.example.toyproject.interceptor;

import com.example.toyproject.Service.BoardService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@Component
@Slf4j
public class BoardDetailInterceptor extends HandlerInterceptorAdapter {
    @Autowired
    BoardService boardService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //PathVariable 인터셉터에서 받기
        Map<String, Long> pathVariables = (Map<String, Long>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        log.info(pathVariables.get("id") + "");
        String id = pathVariables.get("id") + "";
        String page = request.getParameter("page");

        if (request.getSession().getAttribute("pwmatch") == null) {
            if (boardService.hiddencheck(id).get("result") == 1) {
                if (Integer.parseInt(boardService.authcheck(id).get("result")) == 0 ||
                        Integer.parseInt(boardService.authcheck(id).get("result")) == 3) {
                    //비밀글이고 , 손님이나 일반사용자이면 비밀번호체크
                    response.sendRedirect("./blogpwcheck?id=" + id + "&page=" + page);
                }
            }
        }
        else {
            request.getSession().removeAttribute("pwmatch");
        }

        log.info("address: " + request.getRequestURI());

        return super.preHandle(request, response, handler);
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        super.postHandle(request, response, handler, modelAndView);
    }
}
