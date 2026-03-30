package com.example.board.repository;

import com.example.board.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // 아이디(username)로 회원 정보를 조회하는 메서드
    Optional<User> findByUsername(String username);
}
