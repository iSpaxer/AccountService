package com.example.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "Login request model containing username and password")
public class LoginRequest {

    @Size(min = 8, message = "Password must be at least 8 characters long")
    @Schema(description = "Username", example = "alexandr")
    String username;

    @Size(min = 8, message = "Password must be at least 8 characters long")
    @Schema(description = "User password", example = "password123")
    private String password;

}
