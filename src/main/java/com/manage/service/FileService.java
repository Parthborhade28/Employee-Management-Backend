package com.manage.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileService {

	private final String UPLOAD_DIR = "UPLOADS/";

	public String uploadFile(MultipartFile file) throws IOException {
		if (file.isEmpty()) {
			throw new RuntimeException("Please select a file");
		}
		Path uploadPath = Paths.get(UPLOAD_DIR);
		if (!Files.exists(uploadPath)) {
			Files.createDirectories(uploadPath);
		}
		String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
		Path filePath = uploadPath.resolve(fileName);
		Files.copy(file.getInputStream(), filePath);
		return fileName;

	}

	public Resource downloadFile(String fileName) throws IOException {
		Path path = Paths.get(UPLOAD_DIR).resolve(fileName);
		return new UrlResource(path.toUri());
	}
}
