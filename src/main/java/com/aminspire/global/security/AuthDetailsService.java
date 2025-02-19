package com.aminspire.global.security;

import com.aminspire.domain.user.domain.user.User;
import com.aminspire.domain.user.repository.UserRepository;
import com.aminspire.global.exception.CommonException;
import com.aminspire.global.exception.errorcode.UserErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AuthDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmailAndDeletedAtIsNull(email).orElseThrow(() -> new CommonException(UserErrorCode.USER_NOT_FOUND));

        return new AuthDetails(user);
    }
}