package com.example.crc.util;

import com.example.crc.common.constant.RedisKeyConstants;

public final class RedisLockUtil {

	private RedisLockUtil() {
	}

	public static String uploadLockKey(Long userId, String fileMd5) {
		return RedisKeyConstants.UPLOAD_LOCK.formatted(userId, fileMd5);
	}

	public static String taskLockKey(Long taskId) {
		return RedisKeyConstants.TASK_LOCK.formatted(taskId);
	}
}
