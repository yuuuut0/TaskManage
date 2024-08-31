package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.Project;
import com.example.demo.entity.ProjectByUserId;

@Repository
public interface ProjectByUserIdRepository extends JpaRepository<ProjectByUserId, String>{

	@Query("SELECT p.project FROM ProjectByUserId p WHERE p.user_id = :userId")
    List<Project> findProjectsByUserId(@Param("userId") String userId);
}
