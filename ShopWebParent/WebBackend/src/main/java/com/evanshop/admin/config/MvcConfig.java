package com.evanshop.admin.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		
		exposeDirectory("user-photos", registry);
		exposeDirectory("../category-images", registry);
		exposeDirectory("../brand-logos", registry);
		exposeDirectory("../product-images", registry);

		// User photo source
//		String userPhotoDirName = "user-photos";
//		Path userPhotoDir = Paths.get(userPhotoDirName);
//		
//		String userPhotosPath = userPhotoDir.toFile().getAbsolutePath();
//		
//		registry.addResourceHandler("/" + userPhotoDirName + "/**")
//			.addResourceLocations("file:/" + userPhotosPath + "/");


		// Category image source
//		String catgImagesDirName = "../category-images";
//		Path catgImagesDir = Paths.get(catgImagesDirName);
//		
//		String catgImagesPath = catgImagesDir.toFile().getAbsolutePath();
//		
//		registry.addResourceHandler("/category-images/**")
//			.addResourceLocations("file:/" + catgImagesPath + "/");

		// Brand logo image source
//		String brandLogosDirName = "../brand-logos";
//		Path brandLogosDir = Paths.get(brandLogosDirName);
//		
//		String brandLogosPath = brandLogosDir.toFile().getAbsolutePath();
//		
//		registry.addResourceHandler("/brand-logos/**")
//			.addResourceLocations("file:/" + brandLogosPath + "/");
	}

	private void exposeDirectory(String pathPattern, ResourceHandlerRegistry registry) {
		Path path = Paths.get(pathPattern);
		String absolutePath = path.toFile().getAbsolutePath();
		String logicalPath = pathPattern.replace("..", "") + "/**";

		registry.addResourceHandler(logicalPath)
			.addResourceLocations("file:/" + absolutePath + "/");
	}

}
