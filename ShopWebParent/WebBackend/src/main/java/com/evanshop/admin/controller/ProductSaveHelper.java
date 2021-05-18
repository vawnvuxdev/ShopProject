package com.evanshop.admin.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.evanshop.admin.config.FileUploadUtil;
import com.evanshop.common.entity.Product;
import com.evanshop.common.entity.ProductImage;

public class ProductSaveHelper {

	private static final Logger LOGGER = LoggerFactory.getLogger(ProductSaveHelper.class);

	static void deleteExtraImagesWeredRemovedOnForm(Product product) {
		String extraImageDir = "../product-images/" + product.getId() + "/extras";
		Path dirPath = Paths.get(extraImageDir);

		try {
			Files.list(dirPath).forEach(file -> {
				String fileName = file.toFile().getName();
				if (!product.containsImageName(fileName)) {
					try {
						Files.delete(file);
						LOGGER.info("Deleted extra image: " + fileName);
					} catch (IOException e) {
						LOGGER.error("Could not delete extra iamge: " + fileName);
					}
				}

			});
		} catch (IOException e) {
			LOGGER.error("Could not list directory: " + dirPath);
		}
	}

	static void setExistingExtraImageName(String[] imageIds, String[] imageNames, Product product) {
		if (imageIds == null || imageIds.length == 0)
			return;

		Set<ProductImage> images = new HashSet<>();

		for (int count = 0; count < imageIds.length; count++) {
			Integer id = Integer.parseInt(imageIds[count]);
			String name = imageNames[count];
			images.add(new ProductImage(id, name, product));
		}

		product.setImages(images);
	}

	static void setProductDetail(String[] detailNames, String[] detailValues, Product product) {
		if (detailNames == null || detailNames.length == 0)
			return;
		for (int count = 0; count < detailNames.length; count++) {
			String name = detailNames[count];
			String value = detailValues[count];
			if (!name.isEmpty() && !value.isEmpty()) {
				product.addDetail(name, value);
			}
		}
	}

	static void saveUploadedImages(MultipartFile mainImageMultipartFile, MultipartFile[] extraImageMultipartFile,
			Product savedProduct) throws IOException {
		if (!mainImageMultipartFile.isEmpty()) {
			String fileName = StringUtils.cleanPath(mainImageMultipartFile.getOriginalFilename());
			String uploadDir = "../product-images/" + savedProduct.getId();

			FileUploadUtil.cleanDir(uploadDir);
			FileUploadUtil.saveFile(uploadDir, fileName, mainImageMultipartFile);
		}

		if (extraImageMultipartFile.length > 0) {
			String uploadDir = "../product-images/" + savedProduct.getId() + "/extras";
			for (MultipartFile multipartFile : extraImageMultipartFile) {
				if (multipartFile.isEmpty())
					continue;
				String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
				FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
			}
		}
	}

	static void setNewExtraImagesName(MultipartFile[] extraImageMultipartFile, Product product) {
		if (extraImageMultipartFile.length > 0) {
			for (MultipartFile multipartFile : extraImageMultipartFile) {
				if (!multipartFile.isEmpty()) {
					String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());

					if (!product.containsImageName(fileName)) {
						product.addExtraImage(fileName);
					}

				}
			}
		}
	}

	static void setMainImageName(MultipartFile mainImageMultipartFile, Product product) {
		if (!mainImageMultipartFile.isEmpty()) { // Create new Product
			String fileName = StringUtils.cleanPath(mainImageMultipartFile.getOriginalFilename());
			product.setMainImage(fileName);
		}
	}
}
