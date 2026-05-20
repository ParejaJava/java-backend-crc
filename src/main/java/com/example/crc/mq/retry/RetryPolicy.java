package com.example.crc.mq.retry;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class RetryPolicy {

	private final int maxRetryCount;

	public RetryPolicy(@Value("${crc.task.max-retry-count:3}") int maxRetryCount) {
		this.maxRetryCount = maxRetryCount;
	}

	public boolean canRetry(Integer retryCount) {
		return retryCount == null || retryCount < maxRetryCount;
	}

	public int getMaxRetryCount() {
		return maxRetryCount;
	}
}
