package com.example.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.Approval;
import com.example.demo.entity.Task;
import com.example.demo.entity.UserInfo;

public interface ApprovalRepository extends JpaRepository<Approval, Integer>{
	
	public List<Approval> findAllByTask(Task task);
	
	public Optional<Approval> findByTaskAndApproverFlg(Task task, boolean approverFlg);
	
	public Optional<Approval> findByTaskAndAssigneeFlg(Task task, boolean assigneeFlg);
	
    List<Approval> findAllByApproverAndApproverFlgAndProjectId(UserInfo approver, boolean ApproverFlg, String projectId);
	
    List<Approval> findAllByAssigneeAndAssigneeFlgAndProjectId(UserInfo assignee, boolean assigeneeFlg, String projectId);
    
}
