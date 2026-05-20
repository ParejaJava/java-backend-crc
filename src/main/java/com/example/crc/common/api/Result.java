package com.example.crc.common.api;

import java.time.Instant;

public class Result<T> {

	private int code;
	private String message;
	private T data;
	private Instant timestamp;

	public Result() {
		this.timestamp = Instant.now();
	}

	private Result(ResultCode resultCode, String message, T data) {
		this.code = resultCode.getCode();
		this.message = message;
		this.data = data;
		this.timestamp = Instant.now();
	}

	public static <T> Result<T> success(T data) {
		return new Result<>(ResultCode.SUCCESS, ResultCode.SUCCESS.getMessage(), data);
	}

	public static <T> Result<T> success() {
		return success(null);
	}

	public static <T> Result<T> failure(ResultCode resultCode, String message) {
		return new Result<>(resultCode, message, null);
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	public Instant getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Instant timestamp) {
		this.timestamp = timestamp;
	}
}
