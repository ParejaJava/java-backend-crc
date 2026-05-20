package com.example.crc.service;

import com.example.crc.dto.upload.CompleteMultipartUploadRequest;
import com.example.crc.dto.upload.InitMultipartUploadRequest;
import com.example.crc.dto.upload.UploadPartRequest;
import com.example.crc.vo.FileUploadVO;
import java.util.List;

public interface FileService {

	FileUploadVO initMultipartUpload(InitMultipartUploadRequest request);

	FileUploadVO uploadPart(UploadPartRequest request);

	List<Integer> listUploadedParts(Long fileId);

	FileUploadVO completeMultipartUpload(CompleteMultipartUploadRequest request);

	FileUploadVO getFile(Long fileId);
}
