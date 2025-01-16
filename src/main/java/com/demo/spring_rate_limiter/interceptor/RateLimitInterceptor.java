package com.demo.spring_rate_limiter.interceptor;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RateLimitInterceptor implements HandlerInterceptor {

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws
		Exception {
		System.out.println(request.getRemoteAddr());
		return HandlerInterceptor.super.preHandle(request, response, handler);
	}

	// 남은 카운트를 구하는 함수
	// 지금 요청이 가능한지 리턴
	// 충전하는 스케줄러
}
