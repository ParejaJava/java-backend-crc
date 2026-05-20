package com.example.crc.controller;

import com.example.crc.common.api.Result;
import com.example.crc.dto.upload.CompleteMultipartUploadRequest;
import com.example.crc.dto.upload.InitMultipartUploadRequest;
import com.example.crc.dto.upload.UploadPartRequest;
import com.example.crc.service.FileService;
import com.example.crc.vo.FileUploadVO;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/files")
public class FileController {

	private final FileService fileService;

	public FileController(FileService fileService) {
		this.fileService = fileService;
	}

	@PostMapping("/init-multipart")
	public Result<FileUploadVO> initMultipart(@Valid @RequestBody InitMultipartUploadRequest request) {
		return Result.success(fileService.initMultipartUpload(request));
	}

	@PostMapping("/upload-part")
	public Result<FileUploadVO> uploadPart(@Valid @ModelAttribute UploadPartRequest request) {
		return Result.success(fileService.uploadPart(request));
	}

	@GetMapping("/uploaded-parts")
	public Result<List<Integer>> uploadedParts(@RequestParam Long fileId) {
		return Result.success(fileService.listUploadedParts(fileId));
	}

	@PostMapping("/complete-multipart")
	public Result<FileUploadVO> completeMultipart(@Valid @RequestBody CompleteMultipartUploadRequest request) {
		return Result.success(fileService.completeMultipartUpload(request));
	}

	@GetMapping("/{fileId}")
	public Result<FileUploadVO> getFile(@PathVariable Long fileId) {
		return Result.success(fileService.getFile(fileId));
	}
}
