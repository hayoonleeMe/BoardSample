package com.example.board.service;

import com.example.board.dto.SignupRequestDto;
import com.example.board.entity.User;
import com.example.board.entity.UserRoleEnum;
import com.example.board.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${admin.token}")
    private String adminToken;

    /*
     * passwordEncoder.encode(): 이전 단계에서 WebSecurityConfig 에 등록해 두었던 암호화 객체를 주입받아 사용한다.
     * 데이터베이스가 해킹당하더라도 사용자의 원래 비밀번호를 절대 알 수 없도록 안전하게 변환한다.
     */
    @Transactional
    public void signup(SignupRequestDto requestDto) {
        String username = requestDto.getUsername();

        // 1. 비밀번호 암호화 (사용자가 입력한 평문 비밀번호를 BCrypt 로 암호화)
        String password = passwordEncoder.encode(requestDto.getPassword());

        // 2. 회원 중복 확인
        Optional<User> checkUsername = userRepository.findByUsername(username);
        if (checkUsername.isPresent()) {
            throw new IllegalArgumentException("중복된 사용자가 존재합니다.");
        }

        // 3. 사용자 권한(Role) 확인
        UserRoleEnum role = UserRoleEnum.USER;
        if (requestDto.isAdmin()) {
            if (!adminToken.equals(requestDto.getAdminToken())) {
                throw new IllegalArgumentException("관리자 암호가 틀려 등록이 불가능합니다.");
            }
            role = UserRoleEnum.ADMIN;
        }

        // 4. 회원 엔티티 생성 및 데이터베이스 저장
        User user = new User(username, password, role);
        userRepository.save(user);
    }
}
