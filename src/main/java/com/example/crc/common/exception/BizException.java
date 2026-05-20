package com.example.crc.common.exception;

import com.example.crc.common.api.ResultCode;

public class BizException extends RuntimeException {

	private final ResultCode resultCode;

	public BizException(String message) {
		this(ResultCode.BAD_REQUEST, message);
	}

	public BizException(ResultCode resultCode, String message) {
		super(message);
		this.resultCode = resultCode;
	}

	public ResultCode getResultCode() {
		return resultCode;
	}
}
