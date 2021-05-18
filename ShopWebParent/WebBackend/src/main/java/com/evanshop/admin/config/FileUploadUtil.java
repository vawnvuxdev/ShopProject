package com.evanshop.admin.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class FileUploadUtil {
	
	// Create logger from slf4j
	// LoggerFactory from slf4j
	private static final Logger LOGGER = LoggerFactory.getLogger(FileUploadUtil.class);
	
	
	// Save user photo to user file
	public static void saveFile(String uploadDir, String fileName,
			MultipartFile multipartFile) throws IOException {
		Path uploadPath = Paths.get(uploadDir);
		
		if(!Files.exists(uploadPath)) {
			Files.createDirectories(uploadPath);
		}
		
		try(InputStream inputStream = multipartFile.getInputStream()){
			Path filePath = uploadPath.resolve(fileName);
			Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException ex) {
			throw new IOException("Could not save file: " + fileName, ex);
		}
	}
	
	
	// Clear old pictures, files in user file
	public static void cleanDir(String dir) {
		Path dirPath = Paths.get(dir);
		
		try {
			Files.list(dirPath).forEach(file -> {
					if (!Files.isDirectory(file)) {
						try {
							Files.delete(file);
						} catch (IOException ex) {
							LOGGER.error("Could not delete file: " + file);
//							System.out.println("Could not delete file: " + file);
						}
					}
			});
		} catch (IOException ex) {
			LOGGER.error("Could not delete file: " + dirPath);
//			System.out.println("Could not list directory: " + dir);
		}
	}


	public static void removeDir(String catgDir) {
		cleanDir(catgDir);
		
		try {
			Files.delete(Paths.get(catgDir));
		} catch (IOException e) {
			LOGGER.error("Could not remove directory: " + catgDir);
		}
	}
}

