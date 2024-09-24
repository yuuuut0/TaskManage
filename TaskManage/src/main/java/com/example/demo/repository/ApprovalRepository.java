package com.example.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.Approval;
import com.example.demo.entity.Task;

public interface ApprovalRepository extends JpaRepository<Approval, Integer>{
	
	public List<Approval> findAllByTask(Task task);
	
	public Optional<Approval> findByTaskAndApproverFlg(Task task, boolean approverFlg);
	
	public Optional<Approval> findByTaskAndAssigneeFlg(Task task, boolean assigneeFlg);
	
}
