package com.example.demo.repository;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.entity.UserInfo;

@Repository
public interface UserRepository extends JpaRepository<UserInfo, String>{

	@Modifying
	@Transactional
	@Query("UPDATE UserInfo u SET u.lastActiveAt = ?1 WHERE u.userId = ?2")
	void updateLastActiveAt(LocalDateTime activeAt, String userId);
}
