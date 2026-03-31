package com.example.board.security;

import com.example.board.entity.User;
import com.example.board.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/*
 * UserDetailsServiceImpl: 토큰에서 추출한 사용자 아이디(username)를 바탕으로 데이터베이스(UserRepository)에서 실제 회원을 조회한다.
 * 조회가 성공하면 그 회원 정보를 앞서 만든 UserDetailsImpl 객체에 담아 시큐리티에게 넘겨주는 역할을 한다.
 */
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + username));
        return new UserDetailsImpl(user);
    }
}
