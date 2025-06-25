package com.example.service;

import com.example.dto.PostDto;
import com.example.entity.StatusType;
import com.example.entity.User;
import com.example.rep.PostRepository;
import com.example.util.EntityMapper;
import com.example.util.exception.BadRequestException;
import com.example.util.exception.NotFoundException;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final EntityMapper mapper;
    private final EntityManager entityManager;

    public PostService(PostRepository postRepository, EntityMapper mapper, EntityManager entityManager) {
        this.postRepository = postRepository;
        this.mapper = mapper;
        this.entityManager = entityManager;
    }

    public PostDto createPost(PostDto dto, Long userId) {
        var post = mapper.mapToEntity(dto);

        post.setUser(entityManager.getReference(User.class, userId));
        return mapper.mapToDto(postRepository.save(post));
    }

    public List<PostDto> getPosts(Long userId) {
        var listPosts = postRepository.findByUserIdAndStatus(userId, StatusType.ACTIVE);
        if (listPosts.isEmpty()) {
            throw new NotFoundException("Posts for User by id=" + userId + " not found!");
        }
        return mapper.mapToDto(listPosts);
    }

    public PostDto updatePost(PostDto dto, Long userId) {
        var post = postRepository.findActiveByIdAndUserId(dto.getId(), userId)
                .orElseThrow(() -> new NotFoundException(userId));
        mapper.map(post, dto);
        return mapper.mapToDto(postRepository.save(post));
    }

    public void deletePost(Long postId, Long userId) {
        var count = postRepository.deleteByIdAndUserId(postId, userId);
        if (count == 0) {
            throw new BadRequestException("Post not found!");
        }
    }
}
