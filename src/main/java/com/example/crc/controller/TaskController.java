package com.example.crc.controller;

import com.example.crc.common.api.Result;
import com.example.crc.dto.task.CreateTaskRequest;
import com.example.crc.service.TaskService;
import com.example.crc.vo.ComputeResultVO;
import com.example.crc.vo.TaskStatusVO;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

	private final TaskService taskService;

	public TaskController(TaskService taskService) {
		this.taskService = taskService;
	}

	@PostMapping
	public Result<TaskStatusVO> createTask(@Valid @RequestBody CreateTaskRequest request) {
		return Result.success(taskService.createTask(request));
	}

	@GetMapping("/{taskId}")
	public Result<TaskStatusVO> getTask(@PathVariable Long taskId) {
		return Result.success(taskService.getTaskStatus(taskId));
	}

	@GetMapping("/{taskId}/result")
	public Result<ComputeResultVO> getResult(@PathVariable Long taskId) {
		return Result.success(taskService.getTaskResult(taskId));
	}

	@PostMapping("/{taskId}/retry")
	public Result<TaskStatusVO> retryTask(@PathVariable Long taskId) {
		return Result.success(taskService.retryTask(taskId));
	}
}
