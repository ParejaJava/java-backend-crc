package com.example.crc.dto.upload;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class CompleteMultipartUploadRequest {

	@NotNull
	private Long fileId;

	@NotNull
	@Positive
	private Integer totalParts;

	public Long getFileId() {
		return fileId;
	}

	public void setFileId(Long fileId) {
		this.fileId = fileId;
	}

	public Integer getTotalParts() {
		return totalParts;
	}

	public void setTotalParts(Integer totalParts) {
		this.totalParts = totalParts;
	}
}
