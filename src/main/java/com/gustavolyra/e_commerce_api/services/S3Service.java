package com.gustavolyra.e_commerce_api.services;

import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.gustavolyra.e_commerce_api.config.S3Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
public class S3Service {

    @Value("${aws.s3.bucket}")
    private String bucketName;

    private final S3Config amazonS3;

    public S3Service(S3Config amazonS3) {
        this.amazonS3 = amazonS3;
    }

    public String addFileToBucket(MultipartFile file) throws IOException {
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(file.getContentType());
        metadata.setContentDisposition("inline");
        amazonS3.amazonS3().putObject(new PutObjectRequest(bucketName, fileName, file.getInputStream(), metadata));
        return amazonS3.amazonS3().getUrl(bucketName, fileName).toString();


    }


}
