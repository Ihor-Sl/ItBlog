package ua.iate.itblog.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ua.iate.itblog.service.MinioService;

import java.io.InputStream;

@Controller
@RequiredArgsConstructor
public class AvatarController {

    private final MinioService minioService;

    @GetMapping("/avatar/{uuid}")
    public ResponseEntity<InputStreamResource> getAvatar(@PathVariable String uuid) throws Exception {
        InputStream avatarStream = minioService.getAvatar(uuid);
        MediaType mediaType = MediaType.IMAGE_JPEG;
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"avatar.jpg\"")
                .contentType(mediaType)
                .body(new InputStreamResource(avatarStream));
    }
}