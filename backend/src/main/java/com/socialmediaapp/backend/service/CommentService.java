package com.socialmediaapp.backend.service;

import com.socialmediaapp.backend.dto.request.comment.CreateCommentRequest;
import com.socialmediaapp.backend.dto.response.CommentDto;

import java.util.List;

/**
 * Interfaz para el servicio de la entidad Comment.
 */
public interface CommentService {
    CommentDto createComment(CreateCommentRequest request, Long userId);
    CommentDto getCommentById(Long id);
    List<CommentDto> getCommentsByPostId(Long postId);
    void deleteComment(Long id, Long userId);
}
