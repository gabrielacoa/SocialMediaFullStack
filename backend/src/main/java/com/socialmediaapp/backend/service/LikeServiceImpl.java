package com.socialmediaapp.backend.service;

import com.socialmediaapp.backend.model.Like;
import com.socialmediaapp.backend.repository.LikeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implementación del servicio para la entidad Like.
 */
@Service
public class LikeServiceImpl implements LikeService {

    @Autowired
    private LikeRepository likeRepository;

    @Override
    public Like addLike(Like like) {
        return likeRepository.save(like);
    }

    @Override
    public List<Like> getLikesByPostId(Long postId) {
        return likeRepository.findByPostId(postId);
    }

    @Override
    public List<Like> getLikesByUserId(Long userId) {
        return likeRepository.findByUserId(userId);
    }

    @Override
    public void removeLike(Long id) {
        Like like = likeRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Like not found"));
        likeRepository.deleteById(like.getId());
    }
}
