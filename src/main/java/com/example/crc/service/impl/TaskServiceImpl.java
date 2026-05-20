package com.example.crc.service.impl;

import com.example.crc.common.constant.TaskStatus;
import com.example.crc.dto.task.CreateTaskRequest;
import com.example.crc.service.TaskService;
import com.example.crc.vo.ComputeResultVO;
import com.example.crc.vo.TaskStatusVO;
import org.springframework.stereotype.Service;

@Service
public class TaskServiceImpl implements TaskService {

	@Override
	public TaskStatusVO createTask(CreateTaskRequest request) {
		TaskStatusVO vo = new TaskStatusVO();
		vo.setTaskId(System.currentTimeMillis());
		vo.setStatus(TaskStatus.CREATED.name());
		vo.setProgress(0);
		vo.setMessage("compute task created");
		return vo;
	}

	@Override
	public TaskStatusVO getTaskStatus(Long taskId) {
		TaskStatusVO vo = new TaskStatusVO();
		vo.setTaskId(taskId);
		vo.setStatus(TaskStatus.CREATED.name());
		vo.setProgress(0);
		vo.setMessage("task persistence is not connected yet");
		return vo;
	}

	@Override
	public ComputeResultVO getTaskResult(Long taskId) {
		ComputeResultVO vo = new ComputeResultVO();
		vo.setTaskId(taskId);
		vo.setResultJson("{}");
		return vo;
	}

	@Override
	public TaskStatusVO retryTask(Long taskId) {
		TaskStatusVO vo = new TaskStatusVO();
		vo.setTaskId(taskId);
		vo.setStatus(TaskStatus.RETRYING.name());
		vo.setProgress(0);
		vo.setMessage("task retry requested");
		return vo;
	}
}
