package com.example.PostService.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
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

    @NotNull
    @Schema(description = "Username", example = "alexandr")
    String username;

    @NotNull
    @Size(min = 8, message = "Несоответствующая длина пароля")
    @Schema(description = "User password", example = "password123")
    String password;

}
