package com.example.PostService.util;

import com.example.PostService.dto.PostDto;
import com.example.PostService.dto.UserDto;
import com.example.PostService.entity.Post;
import com.example.PostService.entity.User;
import org.modelmapper.Conditions;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ModelMapperExt extends ModelMapper implements EntityMapper {

    public ModelMapperExt() {
        this.getConfiguration()
                .setFieldMatchingEnabled(true)
                .setPropertyCondition(Conditions.isNotNull())
                .setFieldAccessLevel(org.modelmapper.config.Configuration.AccessLevel.PRIVATE);
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
