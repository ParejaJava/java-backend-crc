package com.example.crc.mq.consumer;

import com.example.crc.dto.task.MatlabComputeResult;
import com.example.crc.mq.message.ComputeTaskMessage;
import com.example.crc.service.MatlabComputeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ComputeTaskConsumer {

	private static final Logger log = LoggerFactory.getLogger(ComputeTaskConsumer.class);

	private final MatlabComputeService matlabComputeService;

	public ComputeTaskConsumer(MatlabComputeService matlabComputeService) {
		this.matlabComputeService = matlabComputeService;
	}

	public void consume(ComputeTaskMessage message) {
		log.info("[CRC_TASK_START] taskId={}, fileId={}, userId={}",
				message.getTaskId(), message.getFileId(), message.getUserId());
		MatlabComputeResult result = matlabComputeService.runCompute(message);
		log.info("[CRC_MATLAB_EXIT] taskId={}, exitCode={}, success={}",
				message.getTaskId(), result.getExitCode(), result.isSuccess());
	}
}
