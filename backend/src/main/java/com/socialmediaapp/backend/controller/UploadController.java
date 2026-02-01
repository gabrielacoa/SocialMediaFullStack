package com.socialmediaapp.backend.controller;

import com.socialmediaapp.backend.service.CloudinaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

/**
 * Controlador para subida de archivos multimedia.
 */
@RestController
@RequestMapping("/api/upload")
public class UploadController {

    @Autowired
    private CloudinaryService cloudinaryService;

    /**
     * Sube una imagen para posts.
     */
    @PostMapping("/image")
    public ResponseEntity<Map<String, String>> uploadImage(@RequestParam("file") MultipartFile file) {
        String imageUrl = cloudinaryService.uploadImage(file, "social-media/posts");

        Map<String, String> response = new HashMap<>();
        response.put("url", imageUrl);
        response.put("message", "Imagen subida exitosamente");

        return ResponseEntity.ok(response);
    }

    /**
     * Sube una imagen de perfil.
     */
    @PostMapping("/profile")
    public ResponseEntity<Map<String, String>> uploadProfileImage(@RequestParam("file") MultipartFile file) {
        String imageUrl = cloudinaryService.uploadImage(file, "social-media/profiles");

        Map<String, String> response = new HashMap<>();
        response.put("url", imageUrl);
        response.put("message", "Imagen de perfil subida exitosamente");

        return ResponseEntity.ok(response);
    }

    /**
     * Sube un video para reels/stories.
     */
    @PostMapping("/video")
    public ResponseEntity<Map<String, String>> uploadVideo(@RequestParam("file") MultipartFile file) {
        String videoUrl = cloudinaryService.uploadVideo(file, "social-media/videos");

        Map<String, String> response = new HashMap<>();
        response.put("url", videoUrl);
        response.put("message", "Video subido exitosamente");

        return ResponseEntity.ok(response);
    }
}
