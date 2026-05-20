package com.example.crc.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ComputeResult {

	private Long id;
	private Long taskId;
	private Long userId;
	private Long fileId;
	private BigDecimal crcIndex;
	private String emotionLabel;
	private String resultJson;
	private String reportObjectName;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getTaskId() {
		return taskId;
	}

	public void setTaskId(Long taskId) {
		this.taskId = taskId;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getFileId() {
		return fileId;
	}

	public void setFileId(Long fileId) {
		this.fileId = fileId;
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

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}
}
