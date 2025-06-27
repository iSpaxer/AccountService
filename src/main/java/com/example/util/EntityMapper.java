package com.example.util;

import com.example.dto.PostDto;
import com.example.dto.UserDto;
import com.example.dto.Views;
import com.example.entity.Post;
import com.example.entity.User;

import java.util.List;

public interface EntityMapper {
    default UserDto mapToDto(User entity) {
        return new UserDto(
                entity.getId(),
                entity.getStatus(),
                entity.getCreatedDate(),
                entity.getLastUpdateDate(),
                entity.getUsername(),
                entity.getDescription()
        );
    }

    UserDto mapToDto(User user, Class<? extends Views.BaseView> view);


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

    List<PostDto> mapToDto(List<Post> entityList);

    default Post mapToEntity(PostDto dto) {
        return new Post(
                dto.getId(),
                dto.getMessage()
        );
    }

    List<Post> mapToEntity(List<PostDto> dtoList);

    default Post map(Post entity, PostDto dto) {
        return null;
    }

}
