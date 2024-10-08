package com.example.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.entity.Approval;
import com.example.demo.entity.Task;
import com.example.demo.entity.UserInfo;

public interface ApprovalRepository extends JpaRepository<Approval, Integer>{
	
	public List<Approval> findAllByTask(Task task);
	
	public Optional<Approval> findByTaskAndApproverFlg(Task task, boolean approverFlg);
	
	public Optional<Approval> findByTaskAndAssigneeFlg(Task task, boolean assigneeFlg);
	
    List<Approval> findAllByApproverAndApproverFlgAndProjectId(UserInfo approver, boolean ApproverFlg, String projectId);
	
    List<Approval> findAllByAssigneeAndAssigneeFlgAndProjectId(UserInfo assignee, boolean assigeneeFlg, String projectId);
    
    List<Approval> findAllByProjectId(String projectId);
    
    void deleteAllByProjectIdAndApprover(String projectId, UserInfo approver);
    
    void deleteAllByProjectIdAndAssignee(String projectId, UserInfo assignee);
    
    List<Approval> findAllByTaskInAndProjectId(List<Task> tasks, String projectId);
    
    @Query("SELECT a FROM Approval a "
    	     + "JOIN a.task t "
    	     + "WHERE t.assignedUser.userId = :userId "
    	     + "AND t.projectId = :projectId "
    	     + "AND a.approverFlg = false "
    	     + "AND a.assigneeFlg = false")
    List<Approval> findAllByTaskAssignedUserAndProjectIdAndFlags(@Param("userId") String userId, @Param("projectId") String projectId);
    
    @Query("SELECT a FROM Approval a "
    	     + "JOIN a.task t "
    	     + "JOIN Task pt ON t.parentId = pt.taskId "
    	     + "WHERE pt.assignedUser.userId = :userId "
    	     + "AND pt.projectId = :projectId "
    	     + "AND a.approverFlg = false")
    List<Approval> findAllByParentTaskAssignedUserAndProjectIdAndFlag(@Param("userId") String userId, @Param("projectId") String projectId);


}
