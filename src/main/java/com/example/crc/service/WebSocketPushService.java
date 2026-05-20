package com.example.crc.service;

import com.example.crc.dto.websocket.TaskProgressMessage;

public interface WebSocketPushService {

	void pushToUser(Long userId, TaskProgressMessage message);
}
