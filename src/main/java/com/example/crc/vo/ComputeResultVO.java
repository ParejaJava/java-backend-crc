package com.example.crc.vo;

import java.math.BigDecimal;

public class ComputeResultVO {

	private Long taskId;
	private BigDecimal crcIndex;
	private String emotionLabel;
	private String resultJson;
	private String reportObjectName;

	public Long getTaskId() {
		return taskId;
	}

	public void setTaskId(Long taskId) {
		this.taskId = taskId;
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

	public String getReportObjectName() {
		return reportObjectName;
	}

	public void setReportObjectName(String reportObjectName) {
		this.reportObjectName = reportObjectName;
	}
}
