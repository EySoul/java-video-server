package com.nntu.wehosty;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class WehostyApplication {

    //	@Bean
    //	MultipartConfigElement multipartConfigElement() {
    //		MultipartConfigFactory factory = new MultipartConfigFactory();
    //		factory.setMaxFileSize(DataSize.ofKilobytes(128));
    //		factory.setMaxRequestSize(DataSize.ofKilobytes(128));
    //		return factory.createMultipartConfig();
    //	}
    public static void main(String[] args) {
        SpringApplication.run(WehostyApplication.class, args);
    }
}
