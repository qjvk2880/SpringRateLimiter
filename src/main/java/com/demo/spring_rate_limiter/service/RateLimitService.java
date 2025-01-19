package com.demo.spring_rate_limiter.service;

import java.util.Set;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
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

	@Scheduled(cron = "0 * * * * *") // 매 분 0초마다 실행
	public void refillRateLimitBuckets() {
		String clientIpPattern = TOKEN_BUCKET_KEY + "*";
		Set<String> clientIps = redisTemplate.keys(clientIpPattern);

		if (clientIps == null) {
			return;
		}

		for (String key : clientIps) {
			redisTemplate.opsForValue().set(key, TOKEN_BUCKET_MAX_SIZE);
		}
	}
}
