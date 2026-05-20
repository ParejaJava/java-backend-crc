package com.example.crc.common.constant;

public final class RedisKeyConstants {

	public static final String UPLOAD_LOCK = "crc:upload:lock:%d:%s";
	public static final String TASK_LOCK = "crc:task:lock:%d";
	public static final String TASK_STATUS = "crc:task:status:%d";
	public static final String TASK_PROGRESS = "crc:task:progress:%d";
	public static final String TASK_WS_USER = "crc:task:ws:user:%d";

	private RedisKeyConstants() {
	}
}
