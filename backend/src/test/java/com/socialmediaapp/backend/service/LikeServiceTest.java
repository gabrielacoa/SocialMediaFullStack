package com.socialmediaapp.backend.service;

import com.socialmediaapp.backend.model.PostLiked;
import com.socialmediaapp.backend.model.Post;
import com.socialmediaapp.backend.repository.PostLikedRepository;
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
    private PostLikedRepository postLikedRepository;

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
        PostLiked like = new PostLiked();
        like.setPost(post);

        when(postLikedRepository.save(any(PostLiked.class))).thenReturn(like);

        PostLiked addedLike = likeService.addLike(like);

        assertEquals(1L, addedLike.getPost().getId());
        verify(postLikedRepository, times(1)).save(like);
    }

    @Test
    void testAddLikeWithInvalidData() {
        PostLiked like = new PostLiked();
        like.setPost(null); // ID de post nulo

        when(postLikedRepository.save(any(PostLiked.class))).thenThrow(new IllegalArgumentException("Invalid like data"));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            likeService.addLike(like);
        });

        assertEquals("Invalid like data", exception.getMessage());
    }

    @Test
    void testGetLikesByPostId() {
        PostLiked like = new PostLiked();

        when(postLikedRepository.findByPostId(1L)).thenReturn(List.of(like));

        List<PostLiked> likes = likeService.getLikesByPostId(1L);

        assertEquals(1, likes.size());
        verify(postLikedRepository, times(1)).findByPostId(1L);
    }

    @Test
    void testDeleteLike() {
        PostLiked like = new PostLiked();
        like.setId(1L);

        when(postLikedRepository.findById(1L)).thenReturn(Optional.of(like));
        doNothing().when(postLikedRepository).deleteById(1L);

        likeService.removeLike(1L);

        verify(postLikedRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteNonExistentLike() {
        when(postLikedRepository.findById(99L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            likeService.removeLike(99L);
        });

        assertEquals("Like not found", exception.getMessage());
        verify(postLikedRepository, never()).deleteById(99L);
    }
}
