package com.example.crc.service;

import com.example.crc.dto.task.MatlabComputeResult;
import com.example.crc.mq.message.ComputeTaskMessage;

public interface MatlabComputeService {

	MatlabComputeResult runCompute(ComputeTaskMessage message);
}
