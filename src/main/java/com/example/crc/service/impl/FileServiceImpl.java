package com.example.crc.service.impl;

import com.example.crc.config.MinioConfig;
import com.example.crc.dto.upload.CompleteMultipartUploadRequest;
import com.example.crc.dto.upload.InitMultipartUploadRequest;
import com.example.crc.dto.upload.UploadPartRequest;
import com.example.crc.service.FileService;
import com.example.crc.vo.FileUploadVO;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class FileServiceImpl implements FileService {

	private final MinioConfig minioConfig;

	public FileServiceImpl(MinioConfig minioConfig) {
		this.minioConfig = minioConfig;
	}

	@Override
	public FileUploadVO initMultipartUpload(InitMultipartUploadRequest request) {
		FileUploadVO vo = new FileUploadVO();
		vo.setFileId(System.currentTimeMillis());
		vo.setBucketName(minioConfig.getBucket());
		vo.setObjectName(buildObjectName(request));
		vo.setUploadStatus("INIT");
		return vo;
	}

	@Override
	public FileUploadVO uploadPart(UploadPartRequest request) {
		FileUploadVO vo = new FileUploadVO();
		vo.setFileId(request.getFileId());
		vo.setBucketName(minioConfig.getBucket());
		vo.setUploadStatus("PART_UPLOADED");
		return vo;
	}

	@Override
	public List<Integer> listUploadedParts(Long fileId) {
		return List.of();
	}

	@Override
	public FileUploadVO completeMultipartUpload(CompleteMultipartUploadRequest request) {
		FileUploadVO vo = new FileUploadVO();
		vo.setFileId(request.getFileId());
		vo.setBucketName(minioConfig.getBucket());
		vo.setUploadStatus("COMPLETED");
		return vo;
	}

	@Override
	public FileUploadVO getFile(Long fileId) {
		FileUploadVO vo = new FileUploadVO();
		vo.setFileId(fileId);
		vo.setBucketName(minioConfig.getBucket());
		vo.setUploadStatus("UNKNOWN");
		return vo;
	}

	private String buildObjectName(InitMultipartUploadRequest request) {
		String date = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
		return request.getUserId() + "/" + date + "/" + request.getFileMd5() + "/" + request.getOriginalFilename();
	}
}
