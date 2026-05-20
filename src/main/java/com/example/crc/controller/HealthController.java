package com.example.crc.controller;

import com.example.crc.common.api.Result;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/health")
public class HealthController {

	@GetMapping
	public Result<Map<String, String>> health() {
		return Result.success(Map.of("status", "UP"));
	}

	@GetMapping("/mysql")
	public Result<Map<String, String>> mysql() {
		return Result.success(Map.of("status", "PENDING"));
	}

	@GetMapping("/redis")
	public Result<Map<String, String>> redis() {
		return Result.success(Map.of("status", "PENDING"));
	}

	@GetMapping("/rabbitmq")
	public Result<Map<String, String>> rabbitmq() {
		return Result.success(Map.of("status", "PENDING"));
	}

	@GetMapping("/minio")
	public Result<Map<String, String>> minio() {
		return Result.success(Map.of("status", "PENDING"));
	}
}
