package com.example.crc.common.exception;

import com.example.crc.common.api.Result;
import com.example.crc.common.api.ResultCode;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(BizException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public Result<Void> handleBizException(BizException exception) {
		return Result.failure(exception.getResultCode(), exception.getMessage());
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public Result<Void> handleValidationException(MethodArgumentNotValidException exception) {
		String message = exception.getBindingResult().getFieldErrors().stream()
				.findFirst()
				.map(error -> error.getField() + " " + error.getDefaultMessage())
				.orElse(ResultCode.BAD_REQUEST.getMessage());
		return Result.failure(ResultCode.BAD_REQUEST, message);
	}

	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public Result<Void> handleException(Exception exception) {
		return Result.failure(ResultCode.INTERNAL_ERROR, exception.getMessage());
	}
}
