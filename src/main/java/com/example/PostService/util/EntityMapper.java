package com.example.PostService.util;

import com.example.PostService.dto.AbstractDto;
import com.example.PostService.dto.UserDto;
import com.example.PostService.entity.User;

public interface EntityMapper {
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
}
