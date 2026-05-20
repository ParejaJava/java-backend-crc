package com.example.crc.service.impl;

import com.example.crc.dto.task.MatlabComputeResult;
import com.example.crc.mq.message.ComputeTaskMessage;
import com.example.crc.service.MatlabComputeService;
import org.springframework.stereotype.Service;

@Service
public class MatlabComputeServiceImpl implements MatlabComputeService {

	@Override
	public MatlabComputeResult runCompute(ComputeTaskMessage message) {
		MatlabComputeResult result = new MatlabComputeResult();
		result.setSuccess(false);
		result.setExitCode(-1);
		result.setMessage("MATLAB ProcessBuilder integration is not connected yet");
		return result;
	}
}
