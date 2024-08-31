package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.User;
import com.example.demo.entity.UserByProjectId;

@Repository
public interface UserByProjectIdRepository extends JpaRepository<UserByProjectId, String>{

	@Query("SELECT u.user FROM UserByProjectId u WHERE u.projectId = :projectId")
    List<User> findUsersByProjectId(@Param("projectId") String projectId);
}
