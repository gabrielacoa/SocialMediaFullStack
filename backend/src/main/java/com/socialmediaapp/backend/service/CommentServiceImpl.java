package com.socialmediaapp.backend.service;

import com.socialmediaapp.backend.model.Comment;
import com.socialmediaapp.backend.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implementación del servicio para la entidad Comment.
 */
@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Override
    public Comment createComment(Comment comment) {
        return commentRepository.save(comment);
    }

    @Override
    public List<Comment> getCommentsByPostId(Long postId) {
        return commentRepository.findByPostId(postId);
    }

    @Override
    public void deleteComment(Long id) {
        Comment comment = commentRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Comment not found"));
        commentRepository.deleteById(comment.getId());
    }

    public Comment updateComment(Long id, Comment comment) {
        return commentRepository.findById(id).map(existingComment -> {
            existingComment.setContent(comment.getContent());
            return commentRepository.save(existingComment);
        }).orElseThrow(() -> new RuntimeException("Comment not found"));
    }
}
