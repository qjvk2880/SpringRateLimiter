package com.demo.spring_rate_limiter.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RateLimitService {
	private final RedisTemplate<String, Object> redisTemplate;

	private final static String TOKEN_BUCKET_KEY = "TOCKEN_BUCKET-";
	private final static long TOKEN_BUCKET_MAX_SIZE = 10;
	private final static long TOKEN_BUCKET_REFILL_PERIOD = 60;

	public boolean exceededLimitRate(String clientIp) {
		String remainTokenValue = getRemainTokenValue(clientIp);

		Long remainTokenCount;
		if (remainTokenValue == null) {
			remainTokenCount = createToken(clientIp);
		} else {
			remainTokenCount = Long.parseLong(remainTokenValue);
		}

		if (remainTokenCount <= 0) {
			return true;
		} else {
			redisTemplate.opsForValue().decrement(getClientKey(clientIp));
			return false;
		}
	}

	private Long createToken(String clientIp) {
		String clientKey = getClientKey(clientIp);
		redisTemplate.opsForValue().set(clientKey, String.valueOf(TOKEN_BUCKET_MAX_SIZE));

		return TOKEN_BUCKET_MAX_SIZE;
	}

	private String getRemainTokenValue(String clientIp) {
		String clientKey = getClientKey(clientIp);
		return (String)redisTemplate.opsForValue().get(clientKey);
	}

	private String getClientKey(String clientIp) {
		return TOKEN_BUCKET_KEY + clientIp;
	}
}
