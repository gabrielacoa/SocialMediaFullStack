package com.socialmediaapp.backend.service;

import com.socialmediaapp.backend.model.Post;
import com.socialmediaapp.backend.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implementación del servicio para la entidad Post.
 */
@Service
public class PostServiceImpl implements PostService {

    @Autowired
    private PostRepository postRepository;

    @Override
    public Post createPost(Post post) {
        return postRepository.save(post);
    }

    @Override
    public Post getPostById(Long id) {
        return postRepository.findById(id).orElse(null);
    }

    @Override
    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    @Override
    public List<Post> getPostsByUserId(Long userId) {
        return postRepository.findByUserId(userId);
    }

    @Override
    public void deletePost(Long id) {
        Post post = postRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Post not found"));
        postRepository.deleteById(post.getId());
    }

    public Post updatePost(Long id, Post post) {
        return postRepository.findById(id).map(existingPost -> {
            existingPost.setContent(post.getContent());
            return postRepository.save(existingPost);
        }).orElseThrow(() -> new RuntimeException("Post not found"));
    }
}
