package com.example.util;

import com.example.dto.PostDto;
import com.example.dto.UserDto;
import com.example.entity.Post;
import com.example.entity.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.modelmapper.Conditions;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ModelMapperExt extends ModelMapper implements EntityMapper {

    private final ObjectMapper objectMapper;

    @Autowired
    public ModelMapperExt(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.getConfiguration()
                .setFieldMatchingEnabled(true)
                .setPropertyCondition(Conditions.isNotNull())
                .setFieldAccessLevel(org.modelmapper.config.Configuration.AccessLevel.PRIVATE);
    }

    @Override
    public UserDto mapToDto(User entity) {
        return this.map(entity, UserDto.class);
    }

    @Override
    public User mapToEntity(UserDto dto) {
        return this.map(dto, User.class);
    }

    @Override
    public List<PostDto> mapToDto(List<Post> entityList) {
        if (entityList == null) return List.of();
        return entityList.stream().map(this::mapToDto).toList();
    }

    @Override
    public Post mapToEntity(PostDto dto) {
        return this.map(dto, Post.class);
    }

    @Override
    public List<Post> mapToEntity(List<PostDto> dtoList) {
        if (dtoList == null) return List.of();
        return dtoList.stream().map(this::mapToEntity).toList();
    }

    @Override
    public User map(User entity, UserDto dto) {
        this.map(dto, entity);
        return entity;
    }

    @Override
    public Post map(Post entity, PostDto dto) {
        this.map(dto, entity);
        return entity;
    }
}
