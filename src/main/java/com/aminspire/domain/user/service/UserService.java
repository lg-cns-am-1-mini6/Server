package com.aminspire.domain.user.service;

import com.aminspire.domain.user.domain.user.User;
import com.aminspire.domain.user.dto.response.UserResponse;

public interface UserService {

    UserResponse showUser(User suer);
}
