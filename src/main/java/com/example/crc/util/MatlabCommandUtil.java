package com.example.crc.util;

import java.util.List;

public final class MatlabCommandUtil {

	private MatlabCommandUtil() {
	}

	public static List<String> buildBatchCommand(String executable, String functionCall) {
		return List.of(executable, "-batch", functionCall);
	}

	public static List<String> buildRuntimeCommand(String executable, String inputPath, String outputPath, String taskId) {
		return List.of(executable, inputPath, outputPath, taskId);
	}
}
