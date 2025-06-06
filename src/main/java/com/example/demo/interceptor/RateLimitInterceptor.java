package com.example.demo.interceptor;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.example.demo.service.RateLimitService;
import com.example.demo.utils.AuthUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class RateLimitInterceptor implements HandlerInterceptor{
    private final RateLimitService rateLimitService;

    public RateLimitInterceptor(RateLimitService rateLimitService) {
        this.rateLimitService = rateLimitService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        Long userId = AuthUtil.getCurrentUserId();

        if (userId == null){
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            response.getWriter().write("Unauthorized: User ID is missing");
            return false;
        }

        boolean allowed = rateLimitService.isAllowed(userId.toString());
        if (!allowed) {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.getWriter().write("Too Many Requests: Rate limit exceeded");
            return false;
        }
        return true;
    }

}
