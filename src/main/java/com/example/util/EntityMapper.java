package com.example.util;

import com.example.dto.PostDto;
import com.example.dto.UserDto;
import com.example.entity.Post;
import com.example.entity.User;

import java.util.List;

public interface EntityMapper {
    UserDto mapToDto(User entity);

    User mapToEntity(UserDto dto);


    User map(User entity, UserDto dto);

    @Deprecated
    // --------------------------
    default PostDto mapToDto(Post entity) { // todo
        return new PostDto(
                entity.getId(),
                entity.getMessage(),
                entity.getCreatedDate(),
                entity.getLastUpdateDate()
        );
    }

    List<PostDto> mapToDto(List<Post> entityList);

    Post mapToEntity(PostDto dto);

    List<Post> mapToEntity(List<PostDto> dtoList);

    Post map(Post entity, PostDto dto);

}
