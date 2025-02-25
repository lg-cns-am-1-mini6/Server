package com.aminspire.domain.user.service.user;

import com.aminspire.domain.user.domain.user.User;
import com.aminspire.domain.user.dto.request.UserUpdateRequest;
import com.aminspire.domain.user.dto.response.UserResponse;

public interface UserService {

    UserResponse showUser(User user);

    UserResponse updateUser(User user, UserUpdateRequest userUpdateRequest);
}
