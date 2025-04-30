package com.socialmediaapp.backend.service;

import com.socialmediaapp.backend.model.Post;
import com.socialmediaapp.backend.repository.PostRepository;
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

class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private PostServiceImpl postService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreatePost() {
        Post post = new Post();
        post.setContent("Test content");

        when(postRepository.save(any(Post.class))).thenReturn(post);

        Post createdPost = postService.createPost(post);

        assertEquals("Test content", createdPost.getContent());
        verify(postRepository, times(1)).save(post);
    }

    @Test
    void testGetPostById() {
        Post post = new Post();
        post.setId(1L);
        post.setContent("Test content");

        when(postRepository.findById(1L)).thenReturn(Optional.of(post));

        Post foundPost = postService.getPostById(1L);

        assertEquals("Test content", foundPost.getContent());
        verify(postRepository, times(1)).findById(1L);
    }

    @Test
    void testCreatePostWithInvalidData() {
        Post post = new Post();
        post.setContent(""); // Contenido vacío

        when(postRepository.save(any(Post.class))).thenThrow(new IllegalArgumentException("Invalid post data"));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            postService.createPost(post);
        });

        assertEquals("Invalid post data", exception.getMessage());
    }

    @Test
    void testUpdatePost() {
        Post post = new Post();
        post.setId(1L);
        post.setContent("Updated content");

        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(postRepository.save(any(Post.class))).thenReturn(post);

        Post updatedPost = postService.updatePost(1L, post);

        assertEquals("Updated content", updatedPost.getContent());
        verify(postRepository, times(1)).save(post);
    }

    @Test
    void testUpdateNonExistentPost() {
        Post post = new Post();
        post.setId(99L);
        post.setContent("Non-existent post");

        when(postRepository.findById(99L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            postService.updatePost(99L, post);
        });

        assertEquals("Post not found", exception.getMessage());
        verify(postRepository, never()).save(any(Post.class));
    }

    @Test
    void testDeletePost() {
        Post post = new Post();
        post.setId(1L);

        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        doNothing().when(postRepository).deleteById(1L);

        postService.deletePost(1L);

        verify(postRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteNonExistentPost() {
        when(postRepository.findById(99L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            postService.deletePost(99L);
        });

        assertEquals("Post not found", exception.getMessage());
        verify(postRepository, never()).deleteById(99L);
    }

    @Test
    void testGetAllPosts() {
        Post post1 = new Post();
        post1.setContent("Post 1");
        Post post2 = new Post();
        post2.setContent("Post 2");

        when(postRepository.findAll()).thenReturn(List.of(post1, post2));

        List<Post> posts = postService.getAllPosts();

        assertEquals(2, posts.size());
        assertEquals("Post 1", posts.get(0).getContent());
        assertEquals("Post 2", posts.get(1).getContent());
        verify(postRepository, times(1)).findAll();
    }
}