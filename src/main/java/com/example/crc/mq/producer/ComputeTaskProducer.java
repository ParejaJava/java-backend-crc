package com.example.crc.mq.producer;

import com.example.crc.mq.message.ComputeTaskMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ComputeTaskProducer {

	private static final Logger log = LoggerFactory.getLogger(ComputeTaskProducer.class);

	public void submit(ComputeTaskMessage message) {
		log.info("[CRC_MQ_SUBMIT_PENDING] taskId={}, fileId={}, userId={}, retryCount={}",
				message.getTaskId(), message.getFileId(), message.getUserId(), message.getRetryCount());
	}
}
