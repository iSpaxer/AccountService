package com.example.PostService.util;

import com.example.PostService.dto.PostDto;
import com.example.PostService.dto.UserDto;
import com.example.PostService.entity.Post;
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

    // --------------------------

    default PostDto mapToDto(Post entity) {
        return new PostDto(
                entity.getId(),
                entity.getMessage(),
                entity.getCreatedDate(),
                entity.getLastUpdateDate()
        );
    }

    default Post mapToEntity(PostDto dto) {
        return new Post(
                dto.getId(),
                dto.getMessage()
        );
    }
}
