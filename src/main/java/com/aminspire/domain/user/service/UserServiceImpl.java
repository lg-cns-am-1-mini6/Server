package com.aminspire.domain.user.service;

import com.aminspire.domain.user.domain.user.User;
import com.aminspire.domain.user.dto.response.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    @Override
    public UserResponse showUser(User user) {
        return UserResponse.of(user.getEmail(), user.getName());
    }
}
