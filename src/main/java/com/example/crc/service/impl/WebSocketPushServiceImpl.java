package com.example.crc.service.impl;

import com.example.crc.dto.websocket.TaskProgressMessage;
import com.example.crc.service.WebSocketPushService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class WebSocketPushServiceImpl implements WebSocketPushService {

	private static final Logger log = LoggerFactory.getLogger(WebSocketPushServiceImpl.class);

	@Override
	public void pushToUser(Long userId, TaskProgressMessage message) {
		log.info("[CRC_WS_PUSH_PENDING] userId={}, taskId={}, status={}", userId, message.getTaskId(), message.getStatus());
	}
}
