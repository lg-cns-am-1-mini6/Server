package com.aminspire.domain.user.service.sociallogin;

import com.aminspire.domain.user.domain.user.LoginType;
import com.aminspire.domain.user.domain.user.Role;
import com.aminspire.domain.user.domain.user.User;
import com.aminspire.domain.user.dto.response.LoginResponse;
import com.aminspire.domain.user.repository.UserRepository;
import com.aminspire.global.exception.CommonException;
import com.aminspire.global.exception.errorcode.UserErrorCode;
import com.aminspire.global.security.jwt.JwtProvider;
import com.aminspire.global.security.oauht2.google.GoogleClient;
import com.aminspire.global.security.oauht2.google.dto.GoogleProfile;
import com.aminspire.global.security.oauht2.google.dto.GoogleToken;
import com.aminspire.global.security.oauht2.kakao.KakaoClient;
import com.aminspire.global.security.oauht2.kakao.dto.KakaoProfile;
import com.aminspire.global.security.oauht2.kakao.dto.KakaoToken;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SocialLoginServiceImpl implements SocialLoginService {

    @Value("${spring.security.oauth2.client.registration.google.redirect-uri}")
    private String googleRedirectUrl;

    @Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}")
    private String kakaoRedirectUrl;

    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;
    private final GoogleClient googleClient;
    private final KakaoClient kakaoClient;

    @Override
    @Transactional
    public LoginResponse signInWithGoogle(String code, HttpServletResponse response) {

        // 구글로 액세스 토큰 요청하기
        GoogleToken googleAccessToken;
        googleAccessToken = googleClient.getGoogleAccessToken(code, googleRedirectUrl);

        // 구글에 있는 사용자 정보 반환
        GoogleProfile googleProfile = googleClient.getMemberInfo(googleAccessToken);

        // 반환된 정보의 이메일 기반으로 사용자 테이블에서 계정 정보 조회 진행
        String email = googleProfile.email();
        if (email == null) {
            throw new CommonException(UserErrorCode.USER_NOT_FOUND);
        }

        // bussiness logic: 사용자 정보가 이미 있다면 로그인 타입 확인 후 해당 사용자 정보를 반환하고, 없다면 새로운 사용자 정보를 생성하여 반환
        User user =
                userRepository
                        .findByEmail(email)
                        .orElseGet(() -> createUser(email, LoginType.GOOGLE));

        if (user.getLoginType() != LoginType.GOOGLE) {
            throw new CommonException(UserErrorCode.ALREADY_EXIST_USER);
        }

        jwtProvider.createToken(user, response);

        return LoginResponse.of("구글 로그인 성공");
    }

    @Override
    @Transactional
    public LoginResponse signInWithKakao(String code, HttpServletResponse response) {
        // 카카오로 액세스 토큰 요청하기
        KakaoToken kakaoToken = kakaoClient.getAccessTokenFromKakao(code, kakaoRedirectUrl);

        // 카카오에 있는 사용자 정보 반환
        KakaoProfile kakaoProfile = kakaoClient.getMemberInfo(kakaoToken);

        // 반환된 정보의 이메일 기반으로 사용자 테이블에서 계정 정보 조회 진행
        String email = kakaoProfile.kakao_account().email();
        if (email == null) {
            throw new CommonException(UserErrorCode.USER_NOT_FOUND);
        }

        // bussiness logic: 사용자 정보가 이미 있다면 로그인 타입 확인 후 해당 사용자 정보를 반환하고, 없다면 새로운 사용자 정보를 생성하여 반환
        User user =
                userRepository
                        .findByEmail(email)
                        .orElseGet(() -> createUser(email, LoginType.KAKAO));

        if (user.getLoginType() != LoginType.KAKAO) {
            throw new CommonException(UserErrorCode.ALREADY_EXIST_USER);
        }

        jwtProvider.createToken(user, response);

        return LoginResponse.of("카카오 로그인 성공");
    }

    @Transactional
    protected User createUser(String email, LoginType loginType) {

        List<String> names = List.of("민지", "하니", "다니엘", "해린", "혜인");
        String randomName = names.get(ThreadLocalRandom.current().nextInt(names.size()));

        User user =
                User.builder()
                        .email(email)
                        .role(Role.ROLE_USER)
                        .name(randomName)
                        .loginType(loginType)
                        .build();
        return userRepository.save(user);
    }
}
