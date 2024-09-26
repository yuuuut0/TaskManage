package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.Task;
import com.example.demo.entity.UserInfo;

@Repository
public interface TaskRepository extends JpaRepository<Task, Integer>{
	
	@Query("SELECT DISTINCT t.parentId "
			+ "FROM Task t "
			+ "WHERE t.assignedUser.userId = ?1 "
			+ "AND t.projectId = ?2")
	List<Integer> findParentTaskIdList(String userId, String projectId);
	
	List<Task> findAllByParentId(int parentId);
	
	List<Task> findAllByParentIdAndAssignedUser(Integer parentId, UserInfo assignedUser);
	
	List<Task> findAllByProjectIdAndAssignedUserNull(String projectId);
}
