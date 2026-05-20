package com.example.crc.dto.task;

import java.math.BigDecimal;

public class MatlabComputeResult {

	private boolean success;
	private int exitCode;
	private BigDecimal crcIndex;
	private String emotionLabel;
	private String resultJson;
	private String message;

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public int getExitCode() {
		return exitCode;
	}

	public void setExitCode(int exitCode) {
		this.exitCode = exitCode;
	}

	public BigDecimal getCrcIndex() {
		return crcIndex;
	}

	public void setCrcIndex(BigDecimal crcIndex) {
		this.crcIndex = crcIndex;
	}

	public String getEmotionLabel() {
		return emotionLabel;
	}

	public void setEmotionLabel(String emotionLabel) {
		this.emotionLabel = emotionLabel;
	}

	public String getResultJson() {
		return resultJson;
	}

	public void setResultJson(String resultJson) {
		this.resultJson = resultJson;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
