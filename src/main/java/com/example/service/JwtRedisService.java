package com.example.service;

import com.example.dto.jwt.JwtToken;
import com.example.entity.redis.AccountInfo;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JwtRedisService {

    private final RedisTemplate<String, String> redisTemplate;

    public JwtRedisService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void logout(JwtToken jwtToken) {

    }

    public void signIn(JwtToken refreshToken) {

    }

    public boolean checkForAccess(String token) {
        return false;
    }

    public List<AccountInfo> getActiveAccounts(JwtToken token) {
        return null;
    }
}
