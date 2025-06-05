package com.example.PostService.util;

import com.example.PostService.dto.PostDto;
import com.example.PostService.dto.UserDto;
import com.example.PostService.entity.Post;
import com.example.PostService.entity.User;
import org.modelmapper.Conditions;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;


/*
default UserDto mapToDto(User entity) {
        return new UserDto(
            entity.getId(),
            entity.getStatus(),
            entity.getCreatedDate(),
            entity.getLastUpdateDate(),
            entity.getUsername()
        );
    }

    default User mapToEntity(UserDto dto) {
        return new User(
                dto.getId(),
                dto.getUsername()
        );
    }

    default User map(User entity, UserDto dto) {
        return null;
    }
 */

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
    public Post mapToEntity(PostDto dto) {
        return this.map(dto, Post.class);
    }

    @Override
    public User map(User entity, UserDto dto) {
        this.map(dto, entity);
        return entity;
    }
}
