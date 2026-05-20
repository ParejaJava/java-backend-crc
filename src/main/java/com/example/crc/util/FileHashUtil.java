package com.example.crc.util;

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public final class FileHashUtil {

	private static final int BUFFER_SIZE = 8192;

	private FileHashUtil() {
	}

	public static String md5(InputStream inputStream) throws IOException {
		try {
			MessageDigest digest = MessageDigest.getInstance("MD5");
			byte[] buffer = new byte[BUFFER_SIZE];
			int length;
			while ((length = inputStream.read(buffer)) != -1) {
				digest.update(buffer, 0, length);
			}
			return toHex(digest.digest());
		} catch (NoSuchAlgorithmException exception) {
			throw new IllegalStateException("MD5 algorithm is not available", exception);
		}
	}

	private static String toHex(byte[] bytes) {
		StringBuilder builder = new StringBuilder(bytes.length * 2);
		for (byte value : bytes) {
			builder.append(String.format("%02x", value));
		}
		return builder.toString();
	}
}
