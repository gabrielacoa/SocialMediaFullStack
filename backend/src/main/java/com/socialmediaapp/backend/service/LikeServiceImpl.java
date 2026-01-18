package com.socialmediaapp.backend.service;

import com.socialmediaapp.backend.dto.response.PostLikedDto;
import com.socialmediaapp.backend.exception.custom.BadRequestException;
import com.socialmediaapp.backend.exception.custom.ResourceNotFoundException;
import com.socialmediaapp.backend.mapper.PostLikedMapper;
import com.socialmediaapp.backend.model.Post;
import com.socialmediaapp.backend.model.PostLiked;
import com.socialmediaapp.backend.model.User;
import com.socialmediaapp.backend.repository.PostLikedRepository;
import com.socialmediaapp.backend.repository.PostRepository;
import com.socialmediaapp.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementación del servicio para la entidad PostLiked (likes).
 */
@Service
@Transactional
public class LikeServiceImpl implements LikeService {

    @Autowired
    private PostLikedRepository postLikedRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostLikedMapper postLikedMapper;

    @Override
    public PostLikedDto addLike(Long postId, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post", postId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));

        // Verificar si ya existe el like
        Optional<PostLiked> existingLike = postLikedRepository.findByPostIdAndUserId(postId, userId);
        if (existingLike.isPresent()) {
            throw new BadRequestException("Ya le has dado like a este post");
        }

        PostLiked like = new PostLiked();
        like.setPost(post);
        like.setUser(user);
        like.setLikedAt(new Date());

        PostLiked savedLike = postLikedRepository.save(like);
        return postLikedMapper.toDto(savedLike);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PostLikedDto> getLikesByPostId(Long postId) {
        return postLikedRepository.findByPostId(postId)
                .stream()
                .map(postLikedMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PostLikedDto> getLikesByUserId(Long userId) {
        return postLikedRepository.findByUserId(userId)
                .stream()
                .map(postLikedMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public void removeLike(Long postId, Long userId) {
        PostLiked like = postLikedRepository.findByPostIdAndUserId(postId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Like no encontrado para este post y usuario"));
        postLikedRepository.delete(like);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasUserLikedPost(Long postId, Long userId) {
        return postLikedRepository.findByPostIdAndUserId(postId, userId).isPresent();
    }
}
