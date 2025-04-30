package com.socialmediaapp.backend.service;

import com.socialmediaapp.backend.model.Comment;

import java.util.List;

/**
 * Interfaz para el servicio de la entidad Comment.
 */
public interface CommentService {
    Comment createComment(Comment comment);
    List<Comment> getCommentsByPostId(Long postId);
    void deleteComment(Long id);
}
