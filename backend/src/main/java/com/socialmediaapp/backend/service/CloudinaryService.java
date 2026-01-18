package com.socialmediaapp.backend.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.socialmediaapp.backend.exception.custom.BadRequestException;
import com.socialmediaapp.backend.util.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

/**
 * Servicio para gestionar la subida y eliminación de archivos en Cloudinary.
 */
@Service
public class CloudinaryService {

    private static final Logger logger = LoggerFactory.getLogger(CloudinaryService.class);

    @Autowired
    private Cloudinary cloudinary;

    /**
     * Sube una imagen a Cloudinary.
     *
     * @param file   Archivo a subir
     * @param folder Carpeta destino en Cloudinary
     * @return URL pública de la imagen subida
     */
    public String uploadImage(MultipartFile file, String folder) {
        validateImageFile(file);

        try {
            Map<String, Object> uploadParams = ObjectUtils.asMap(
                "folder", folder,
                "resource_type", "image",
                "quality", "auto",
                "fetch_format", "auto"
            );

            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), uploadParams);
            String url = (String) uploadResult.get("secure_url");

            logger.info("Imagen subida exitosamente a Cloudinary: {}", url);
            return url;

        } catch (IOException e) {
            logger.error("Error al subir imagen a Cloudinary", e);
            throw new BadRequestException("Error al subir la imagen: " + e.getMessage());
        }
    }

    /**
     * Sube un video a Cloudinary.
     *
     * @param file   Archivo a subir
     * @param folder Carpeta destino en Cloudinary
     * @return URL pública del video subido
     */
    public String uploadVideo(MultipartFile file, String folder) {
        validateVideoFile(file);

        try {
            Map<String, Object> uploadParams = ObjectUtils.asMap(
                "folder", folder,
                "resource_type", "video",
                "quality", "auto"
            );

            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), uploadParams);
            String url = (String) uploadResult.get("secure_url");

            logger.info("Video subido exitosamente a Cloudinary: {}", url);
            return url;

        } catch (IOException e) {
            logger.error("Error al subir video a Cloudinary", e);
            throw new BadRequestException("Error al subir el video: " + e.getMessage());
        }
    }

    /**
     * Elimina un archivo de Cloudinary usando su public_id.
     *
     * @param publicId ID público del archivo en Cloudinary
     */
    public void deleteFile(String publicId) {
        try {
            Map result = cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            logger.info("Archivo eliminado de Cloudinary: {}, resultado: {}", publicId, result.get("result"));

        } catch (IOException e) {
            logger.error("Error al eliminar archivo de Cloudinary: {}", publicId, e);
            throw new BadRequestException("Error al eliminar el archivo: " + e.getMessage());
        }
    }

    /**
     * Extrae el public_id de una URL de Cloudinary.
     * Ejemplo: https://res.cloudinary.com/demo/image/upload/sample.jpg -> sample
     *
     * @param url URL completa de Cloudinary
     * @return public_id del archivo
     */
    public String extractPublicId(String url) {
        if (url == null || !url.contains("cloudinary.com")) {
            throw new BadRequestException("URL de Cloudinary inválida");
        }

        try {
            String[] parts = url.split("/upload/");
            if (parts.length < 2) {
                throw new BadRequestException("Formato de URL inválido");
            }

            String pathWithExtension = parts[1];
            int lastDotIndex = pathWithExtension.lastIndexOf('.');
            return lastDotIndex > 0 ? pathWithExtension.substring(0, lastDotIndex) : pathWithExtension;

        } catch (Exception e) {
            logger.error("Error al extraer public_id de URL: {}", url, e);
            throw new BadRequestException("Error al procesar la URL de Cloudinary");
        }
    }

    /**
     * Valida que el archivo sea una imagen válida.
     */
    private void validateImageFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("El archivo de imagen es obligatorio");
        }

        if (file.getSize() > Constants.MAX_IMAGE_SIZE) {
            throw new BadRequestException("La imagen no puede exceder 5MB");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new BadRequestException("El archivo debe ser una imagen válida");
        }
    }

    /**
     * Valida que el archivo sea un video válido.
     */
    private void validateVideoFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("El archivo de video es obligatorio");
        }

        if (file.getSize() > Constants.MAX_VIDEO_SIZE) {
            throw new BadRequestException("El video no puede exceder 100MB");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("video/")) {
            throw new BadRequestException("El archivo debe ser un video válido");
        }
    }
}
