package ua.iate.itblog.service;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ua.iate.itblog.exception.ImageProcessingException;

import java.io.InputStream;
import java.net.URLConnection;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImageService {

    public static final String DEFAULT_AVATAR_PATH = "/static/default-avatar.jpg";
    private static final int DEFAULT_PART_SIZE = -1;

    private final MinioClient minioClient;

    @Value("${images.s3Endpoint}")
    private String endpoint;

    @Value("${images.bucketName}")
    private String bucketName;

    public String upload(MultipartFile file) {
        try {
            return upload(file.getInputStream(), FilenameUtils.getExtension(file.getOriginalFilename()));
        } catch (Exception e) {
            log.error("Cannot upload image", e);
            throw new ImageProcessingException("errors.image.upload");
        }
    }

    public String upload(InputStream inputStream, String extension) {
        try {
            String filename = UUID.randomUUID() + "." + extension;
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(filename)
                    .contentType(URLConnection.guessContentTypeFromName(filename))
                    .stream(inputStream, inputStream.available(), DEFAULT_PART_SIZE)
                    .build());
            return filename;
        } catch (Exception e) {
            log.error("Cannot upload image", e);
            throw new ImageProcessingException("errors.image.upload");
        }
    }

    public void delete(String imageFileName) {
        try {
            minioClient.removeObject(RemoveObjectArgs.builder()
                    .bucket(bucketName)
                    .object(imageFileName)
                    .build());
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new ImageProcessingException("errors.image.delete");
        }
    }

    public String buildImageUrl(String imageFileName) {
        if (imageFileName == null || imageFileName.isEmpty()) {
            return DEFAULT_AVATAR_PATH;
        }
        return String.join("/", endpoint, bucketName, imageFileName);
    }
}