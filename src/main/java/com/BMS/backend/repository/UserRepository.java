package com.BMS.backend.repository;

import com.BMS.backend.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // 아무것도 안 써도 'findById'(ID로 찾기) 같은 기본 기능은 작동합니다.
}