package com.doan.service;

import org.springframework.web.multipart.MultipartFile;

public interface MinIOService {
    String getObjectUrl(String bucketName, String objectName);
    String saveMultipartFile(MultipartFile multipartFile);
}
