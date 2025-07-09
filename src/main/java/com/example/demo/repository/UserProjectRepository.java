package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.domain.Member;
import com.example.demo.entity.Project;
import com.example.demo.entity.UserInfo;
import com.example.demo.entity.UserProject;
import com.example.demo.entity.UserProjectId;

@Repository
public interface UserProjectRepository extends JpaRepository<UserProject, UserProjectId>{

	
	@Query("SELECT u.project FROM UserProject u WHERE u.id.userId = :userId")
    List<Project> findProjectsByUserId(@Param("userId") String userId);
	
	@Query("SELECT u.userInfo FROM UserProject u WHERE u.id.projectId = :projectId")
    List<UserInfo> findUsersByProjectId(@Param("projectId") String projectId);
	
    @Query("SELECT new com.example.demo.domain.Member(u.userInfo.userId, u.handle) FROM UserProject u WHERE u.id.projectId = :projectId")
    List<Member> findMemberListByProjectId(@Param("projectId") String projectId);
    
    // 特定のプロジェクト内で handle が存在するかどうかを確認するメソッド
    boolean existsByProjectAndHandle(Project project, String handle);
    
    List<UserProject> findAllByUserInfoInAndProject(List<UserInfo> users, Project project);

	List<UserProject> findAllByProject(Project project);
}
