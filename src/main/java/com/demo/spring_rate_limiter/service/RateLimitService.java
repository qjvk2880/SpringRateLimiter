package com.demo.spring_rate_limiter.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RateLimitService {
	private final RedisTemplate<String, Object> redisTemplate;

	private final static String TOKEN_BUCKET_KEY = "TOKEN_BUCKET-";
	private final static long TOKEN_BUCKET_MAX_SIZE = 10;
	private final static long TOKEN_BUCKET_REFILL_PERIOD = 60;

	public boolean isRateLimitExceeded(String clientIp) {
		String clientKey = getClientKey(clientIp);

		Long remainTokenCount = getRemainTokenCount(clientKey);
		if (remainTokenCount == null) {
			remainTokenCount = createToken(clientKey);
		}

		if (remainTokenCount <= 0) {
			return true;
		}

		redisTemplate.opsForValue().decrement(getClientKey(clientIp));
		return false;
	}

	private Long createToken(String clientKey) {
		redisTemplate.opsForValue().set(clientKey, String.valueOf(TOKEN_BUCKET_MAX_SIZE));
		return TOKEN_BUCKET_MAX_SIZE;
	}

	private Long getRemainTokenCount(String clientKey) {
		String tokenCountValue = (String)redisTemplate.opsForValue().get(clientKey);
		return tokenCountValue != null ? Long.parseLong(tokenCountValue) : null;
	}

	private String getClientKey(String clientIp) {
		return TOKEN_BUCKET_KEY + clientIp;
	}
}
