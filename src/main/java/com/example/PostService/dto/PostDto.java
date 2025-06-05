package com.example.PostService.dto;

import com.example.PostService.entity.User;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

public record PostDto(
        @JsonUnwrapped AbstractDto base,
        String message,
        User user
) {
}
