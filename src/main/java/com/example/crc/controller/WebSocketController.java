package com.example.crc.controller;

import com.example.crc.common.api.Result;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ws")
public class WebSocketController {

	@GetMapping("/info")
	public Result<Map<String, String>> info() {
		return Result.success(Map.of("taskWebSocketPath", "/ws/task/{userId}"));
	}
}
