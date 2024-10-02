package com.example.demo.util;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.entity.Approval;
import com.example.demo.entity.Task;
import com.example.demo.entity.UserProjectId;
import com.example.demo.repository.ApprovalRepository;
import com.example.demo.repository.TaskRepository;
import com.example.demo.repository.UserProjectRepository;
import com.example.demo.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Component
@Transactional(rollbackFor = Exception.class)
@RequiredArgsConstructor
public class UtilToggle {
	
	private final TaskRepository taskDao;
	
	private final UserRepository userDao;
	
	private final ApprovalRepository approvalDao;
	
	private final UserProjectRepository userProjectDao;
	
	
	public void complete(Task task) {
		
		task.setCompletedFlg(true);
		task.setUpdatedAt(LocalDateTime.now());
		task.setCompletedAt(LocalDateTime.now());
		taskDao.save(task);
		if(task.getParentId() != null) {
			var parentTask = taskDao.findById(task.getParentId()).get();
			var parentSubComp = parentTask.getSubCompleted() + 1;
			parentTask.setSubCompleted(parentSubComp);
			
			if(parentSubComp == parentTask.getSubTotal() && !parentTask.isSubmitFlg()) {
				complete(parentTask);
			}
			taskDao.save(parentTask);
		}
	}
	
	public void incomplete(Task task) {
		
		task.setCompletedFlg(false);
		task.setUpdatedAt(LocalDateTime.now());
		taskDao.save(task);
		if(task.getParentId() != null) {
			var parentTask = taskDao.findById(task.getParentId()).get();
			parentTask.setSubCompleted(parentTask.getSubCompleted() - 1);
			
			if(parentTask.isCompletedFlg() && !parentTask.isSubmitFlg()) {
				incomplete(parentTask);
			}
			taskDao.save(parentTask);
		}
	}
	
	public void submit(Task task, String ownerId, String comment){
		
		task.setCompletedFlg(true);
		taskDao.save(task);
		
		var approver = userDao.findById(ownerId).get();
		var assignee = task.getAssignedUser();
		var approval = new Approval();
		approval.setTask(task);
		approval.setApprover(approver);
		approval.setAssignee(assignee);
		approval.setComment(comment);
		approval.setSubmittedAt(LocalDateTime.now());
		approval.setApproverFlg(true);
		approval.setProjectId(task.getProjectId());
		approvalDao.save(approval);
		
		var countRecord = userProjectDao.findById(new UserProjectId(ownerId,task.getProjectId())).get();
		countRecord.setUnapprovedCount(countRecord.getUnapprovedCount() + 1);
		userProjectDao.save(countRecord);
	}
	
	
	public void cancel(Task task, String ownerId) {
	
		task.setCompletedFlg(false);
		taskDao.save(task);
		
		var approval = approvalDao.findByTaskAndApproverFlg(task, true).get();
		approval.setApproverFlg(false);
		approvalDao.save(approval);
		
		var countRecordOpt = userProjectDao.findById(new UserProjectId(ownerId,task.getProjectId()));
		if(countRecordOpt.isPresent()) {
			var countRecord = countRecordOpt.get();
			countRecord.setUnapprovedCount(countRecord.getUnapprovedCount() - 1);
			userProjectDao.save(countRecord);
		}
	}
	
	
	public void updateParentTaskOnCreate(Task task) {
		var parentTask = taskDao.findById(task.getParentId()).get();
		parentTask.setSubTotal(parentTask.getSubTotal()+1);
		parentTask.setUpdatedAt(LocalDateTime.now());
		if(parentTask.isSubmitFlg()) {
			if(parentTask.isCompletedFlg()) {
				var nextTask = taskDao.findById(parentTask.getTaskId()).get();
				cancel(parentTask, nextTask.getAssignedUser().getUserId());
			}else {
				taskDao.save(parentTask);
			}
		}else {
			if(parentTask.isCompletedFlg()) {
				incomplete(parentTask);
			}else {
				taskDao.save(parentTask);
			}
		}
	}

}
