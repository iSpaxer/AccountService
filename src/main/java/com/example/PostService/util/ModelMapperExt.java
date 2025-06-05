package com.example.PostService.util;

import com.example.PostService.dto.UserDto;
import com.example.PostService.entity.User;
import org.modelmapper.Conditions;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;


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
    public User map(User entity, UserDto dto) {
        this.map(dto, entity);
        return entity;
    }
}
