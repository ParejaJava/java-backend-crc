package com.example.crc.service;

import com.example.crc.dto.task.CreateTaskRequest;
import com.example.crc.vo.ComputeResultVO;
import com.example.crc.vo.TaskStatusVO;

public interface TaskService {

	TaskStatusVO createTask(CreateTaskRequest request);

	TaskStatusVO getTaskStatus(Long taskId);

	ComputeResultVO getTaskResult(Long taskId);

	TaskStatusVO retryTask(Long taskId);
}
