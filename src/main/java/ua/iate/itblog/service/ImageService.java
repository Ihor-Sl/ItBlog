package ua.iate.itblog.service;

import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ua.iate.itblog.exception.ImageProccesingException;

import java.io.InputStream;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ImageService {
    private final MinioClient minioClient;
    private final int BUFFER_SIZE = 10485760;

    @Value("${minio.bucketName}")
    private String bucketName;

    public String upload(InputStream inputStream, String contentType) {
        try {
            String id = UUID.randomUUID().toString();
            String extension = getExtension(contentType);
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(id + "." + extension)
                    .contentType(contentType)
                    .stream(inputStream, -1, BUFFER_SIZE)
                    .build());
            return id + "." + extension;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new ImageProccesingException("errors.user.avatar.upload");
        }
    }

    private String getExtension(String contentType) {
        return contentType.split("/")[1];
    }

    public void delete(String imageFileName) {
        try {
            minioClient.removeObject(RemoveObjectArgs.builder()
                    .bucket(bucketName)
                    .object(imageFileName)
                    .build());
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new ImageProccesingException("errors.user.avatar.delete");
        }
    }

    public InputStream getAvatar(String imageFileName) {
        try {
            return minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucketName)
                            .object(imageFileName)
                            .build()
            );
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new ImageProccesingException("errors.user.avatar.get");
        }
    }

    public String getFileName(String fileName){
        return fileName != null ? "/users/avatar/" + fileName : null;
    }
}