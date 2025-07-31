package com.example.rep;

import com.example.entity.StatusType;
import com.example.entity.User;

import java.util.Optional;

public interface UserRepository extends AbstractRepository<User, Long>, UserRepositoryAdvanced {

    Optional<User> findByIdAndStatus(Long id, StatusType status);

    Optional<User> findByUsernameAndStatus(String username, StatusType status);

    Optional<Long> findVersionByUsernameAndStatus(String username, StatusType status);


    Optional<User> findByGitHub_GitHubId(Long gitHubId);

}
