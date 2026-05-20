package com.example.crc.common.constant;

public final class MqConstants {

	public static final String COMPUTE_EXCHANGE = "crc.compute.exchange";
	public static final String COMPUTE_QUEUE = "crc.compute.queue";
	public static final String COMPUTE_RETRY_QUEUE = "crc.compute.retry.queue";
	public static final String COMPUTE_DLQ = "crc.compute.dlq";
	public static final String ROUTING_SUBMIT = "crc.compute.submit";
	public static final String ROUTING_RETRY = "crc.compute.retry";
	public static final String ROUTING_DEAD = "crc.compute.dead";

	private MqConstants() {
	}
}
