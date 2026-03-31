package com.example.board.security;

import com.example.board.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;

/*
 * UserDetailsImpl: 우리가 정의한 User 엔티티를 스프링 시큐리티가 이해할 수 있는 규격인 UserDetails 인터페이스에 맞게 포장하는 어댑터(Adapter) 클래스다.
 * 사용자의 아이디, 비밀번호, 권한 정보를 시큐리티의 형식으로 변환하여 제공한다.
 */
public class UserDetailsImpl implements UserDetails {

    private final User user;

    public UserDetailsImpl(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        String authority = user.getRole().getAuthority();
        SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority(authority);
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(simpleGrantedAuthority);
        return authorities;
    }
}
