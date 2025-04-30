package com.socialmediaapp.backend.service;

import com.socialmediaapp.backend.model.Like;
import com.socialmediaapp.backend.model.Post;
import com.socialmediaapp.backend.repository.LikeRepository;
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

class LikeServiceTest {

    @Mock
    private LikeRepository likeRepository;

    @InjectMocks
    private LikeServiceImpl likeService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddLike() {
        Post post = new Post();
        post.setId(1L);
        Like like = new Like();
        like.setPost(post);

        when(likeRepository.save(any(Like.class))).thenReturn(like);

        Like addedLike = likeService.addLike(like);

        assertEquals(1L, addedLike.getPost().getId());
        verify(likeRepository, times(1)).save(like);
    }

    @Test
    void testAddLikeWithInvalidData() {
        Like like = new Like();
        like.setPost(null); // ID de post nulo

        when(likeRepository.save(any(Like.class))).thenThrow(new IllegalArgumentException("Invalid like data"));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            likeService.addLike(like);
        });

        assertEquals("Invalid like data", exception.getMessage());
    }

    @Test
    void testGetLikesByPostId() {
        Like like = new Like();

        when(likeRepository.findByPostId(1L)).thenReturn(List.of(like));

        List<Like> likes = likeService.getLikesByPostId(1L);

        assertEquals(1, likes.size());
        verify(likeRepository, times(1)).findByPostId(1L);
    }

    @Test
    void testDeleteLike() {
        Like like = new Like();
        like.setId(1L);

        when(likeRepository.findById(1L)).thenReturn(Optional.of(like));
        doNothing().when(likeRepository).deleteById(1L);

        likeService.removeLike(1L);

        verify(likeRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteNonExistentLike() {
        when(likeRepository.findById(99L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            likeService.removeLike(99L);
        });

        assertEquals("Like not found", exception.getMessage());
        verify(likeRepository, never()).deleteById(99L);
    }
}