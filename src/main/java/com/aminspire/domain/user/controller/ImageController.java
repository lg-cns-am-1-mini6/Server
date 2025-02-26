package com.aminspire.domain.user.controller;

import com.aminspire.domain.user.dto.request.ImageRequest;
import com.aminspire.domain.user.dto.response.PresignedUrlResponse;
import com.aminspire.domain.user.service.image.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/image")
public class ImageController {

    private final ImageService imageService;

    @PostMapping("/presigned-url")
    public PresignedUrlResponse saveImage(@RequestBody ImageRequest imageRequest) {
        String preSignedUrl = imageService.getPreSignedUrl("newjeans", imageRequest.imageName());
        return PresignedUrlResponse.of(preSignedUrl);
    }
}
