package com.aminspire.domain.user.service.user;

import com.aminspire.domain.user.domain.user.User;
import com.aminspire.domain.user.dto.request.UserUpdateRequest;
import com.aminspire.domain.user.dto.response.UserResponse;
import com.aminspire.domain.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserResponse showUser(User user) {
        return UserResponse.of(user.getEmail(), user.getName());
    }

    @Override
    @Transactional
    public UserResponse updateUser(User user, UserUpdateRequest userUpdateRequest) {

        if (userUpdateRequest.name() != null) {
            user.updateName(userUpdateRequest.name());
        }

        if (userUpdateRequest.imageUrl() != null) {
            user.updateImageUrl(userUpdateRequest.imageUrl());
        }

        userRepository.save(user);

        return UserResponse.of(user.getEmail(), user.getName());
    }
}
