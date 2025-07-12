package com.example.rep;

import com.example.dto.UserDto;
import com.example.dto.ViewsE;

import java.util.Optional;

public interface UserRepositoryAdvanced {

    Optional<UserDto> findActiveByIdAndTypeView(Long id, ViewsE view);

}
