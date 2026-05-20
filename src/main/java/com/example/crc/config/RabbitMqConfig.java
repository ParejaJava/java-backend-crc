package com.example.crc.config;

import com.example.crc.common.constant.MqConstants;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {

	public String computeExchange() {
		return MqConstants.COMPUTE_EXCHANGE;
	}
}
