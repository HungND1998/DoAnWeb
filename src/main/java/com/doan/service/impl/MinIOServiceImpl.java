package com.doan.service.impl;

import com.doan.configuration.MinIOConfiguration;
import com.doan.service.MinIOService;
import com.doan.utils.FileTypeUtils;
import com.doan.utils.MinioUtil;
import io.minio.MinioClient;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
public class MinIOServiceImpl implements MinIOService {

    private final MinioUtil minioUtil;
    private final MinioClient minioClient;
    private final MinIOConfiguration minioProperties;

    public MinIOServiceImpl(MinioUtil minioUtil, MinioClient minioClient, MinIOConfiguration minioProperties) {
        this.minioUtil = minioUtil;
        this.minioClient = minioClient;
        this.minioProperties = minioProperties;
    }

    @Override
    public String saveMultipartFile(MultipartFile file) {
        String bucketName = minioProperties.getBucketName();

        String fileType = FileTypeUtils.getFileType(file);
        String fileName = file.getOriginalFilename();

        String objectName = UUID.randomUUID().toString().replaceAll("-", "")
                + fileName.substring(fileName.lastIndexOf("."));


        minioUtil.putObject(bucketName, file, objectName, fileType);
        String url = getObjectUrl(bucketName, objectName);

        if (!StringUtils.isEmpty(url)) {
            return url.split("\\?")[0];
        }
        return url;
    }

    @Override
    public String getObjectUrl(String bucketName, String objectName) {
        return minioUtil.getObjectUrl(bucketName, objectName);
    }
}
