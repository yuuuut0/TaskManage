package com.example.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.Project;
import com.example.demo.entity.Task;

@Repository
public interface ProjectRepository extends JpaRepository<Project, String>{

	Optional<Project> findByFirstTask(Task task);
}