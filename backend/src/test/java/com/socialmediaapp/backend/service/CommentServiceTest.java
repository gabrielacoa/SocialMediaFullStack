package com.socialmediaapp.backend.service;

import com.socialmediaapp.backend.model.Comment;
import com.socialmediaapp.backend.repository.CommentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private CommentServiceImpl commentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateComment() {
        Comment comment = new Comment();
        comment.setContent("Test comment");

        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        Comment createdComment = commentService.createComment(comment);

        assertEquals("Test comment", createdComment.getContent());
        verify(commentRepository, times(1)).save(comment);
    }

    @Test
    void testGetCommentsByPostId() {
        Comment comment = new Comment();
        comment.setContent("Test comment");

        when(commentRepository.findByPostId(1L)).thenReturn(List.of(comment));

        List<Comment> comments = commentService.getCommentsByPostId(1L);

        assertEquals(1, comments.size());
        assertEquals("Test comment", comments.get(0).getContent());
        verify(commentRepository, times(1)).findByPostId(1L);
    }

    @Test
    void testCreateCommentWithInvalidData() {
        Comment comment = new Comment();
        comment.setContent(""); // Contenido vacío

        when(commentRepository.save(any(Comment.class))).thenThrow(new IllegalArgumentException("Invalid comment data"));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            commentService.createComment(comment);
        });

        assertEquals("Invalid comment data", exception.getMessage());
    }

    @Test
    void testUpdateComment() {
        Comment comment = new Comment();
        comment.setId(1L);
        comment.setContent("Updated comment");

        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        Comment updatedComment = commentService.updateComment(1L, comment);

        assertEquals("Updated comment", updatedComment.getContent());
        verify(commentRepository, times(1)).save(comment);
    }

    @Test
    void testUpdateNonExistentComment() {
        Comment comment = new Comment();
        comment.setId(99L);
        comment.setContent("Non-existent comment");

        when(commentRepository.findById(99L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            commentService.updateComment(99L, comment);
        });

        assertEquals("Comment not found", exception.getMessage());
        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    void testDeleteComment() {
        Comment comment = new Comment();
        comment.setId(1L);

        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));
        doNothing().when(commentRepository).deleteById(1L);

        commentService.deleteComment(1L);

        verify(commentRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteNonExistentComment() {
        when(commentRepository.findById(99L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            commentService.deleteComment(99L);
        });

        assertEquals("Comment not found", exception.getMessage());
        verify(commentRepository, never()).deleteById(99L);
    }
}