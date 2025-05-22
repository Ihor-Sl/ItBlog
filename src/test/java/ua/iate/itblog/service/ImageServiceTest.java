package ua.iate.itblog.service;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;
import ua.iate.itblog.exception.ImageProcessingException;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ImageServiceTest {

    @Mock
    private MinioClient minioClient;

    @InjectMocks
    private ImageService imageService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(imageService, "endpoint", "http://localhost:9000");
        ReflectionTestUtils.setField(imageService, "bucketName", "test-bucket");
    }

    @Test
    void testUploadMultipartFile_Success() throws Exception {
        byte[] content = "image content".getBytes();
        MockMultipartFile multipartFile = new MockMultipartFile("file", "image.jpg", "image/jpeg", content);

        String result = imageService.upload(multipartFile);

        verify(minioClient).putObject(any(PutObjectArgs.class));
        assertNotNull(result);
        assertTrue(result.endsWith(".jpg"));
    }

    @Test
    void testUploadMultipartFile_Failure() throws Exception {
        MultipartFile brokenFile = mock(MultipartFile.class);
        when(brokenFile.getInputStream()).thenThrow(new RuntimeException("Stream error"));

        assertThrows(ImageProcessingException.class, () -> imageService.upload(brokenFile));
    }

    @Test
    void testUploadInputStream_Success() throws Exception {
        byte[] content = "image content".getBytes();
        InputStream inputStream = new ByteArrayInputStream(content);

        String result = imageService.upload(inputStream, "png");

        verify(minioClient).putObject(any(PutObjectArgs.class));
        assertNotNull(result);
        assertTrue(result.endsWith(".png"));
    }

    @Test
    void testUploadInputStream_Failure() throws Exception {
        InputStream inputStream = mock(InputStream.class);
        when(inputStream.available()).thenThrow(new RuntimeException("Error"));

        assertThrows(ImageProcessingException.class, () -> imageService.upload(inputStream, "png"));
    }

    @Test
    void testDelete_Success() throws Exception {
        doNothing().when(minioClient).removeObject(any(RemoveObjectArgs.class));
        assertDoesNotThrow(() -> imageService.delete("image.png"));
    }

    @Test
    void testDelete_Failure() throws Exception {
        doThrow(new RuntimeException("Delete failed")).when(minioClient).removeObject(any(RemoveObjectArgs.class));
        assertThrows(ImageProcessingException.class, () -> imageService.delete("image.png"));
    }

    @Test
    void testBuildImageUrl_WithFilename() {
        String url = imageService.buildImageUrl("img.jpg");
        assertEquals("http://localhost:9000/test-bucket/img.jpg", url);
    }

    @Test
    void testBuildImageUrl_NullOrEmpty() {
        assertEquals(ImageService.DEFAULT_AVATAR_PATH, imageService.buildImageUrl(null));
        assertEquals(ImageService.DEFAULT_AVATAR_PATH, imageService.buildImageUrl(""));
    }
}
