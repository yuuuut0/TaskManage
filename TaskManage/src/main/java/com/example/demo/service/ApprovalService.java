package com.example.demo.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.constant.ResultMsg;
import com.example.demo.entity.Approval;
import com.example.demo.entity.Task;
import com.example.demo.entity.UserProjectId;
import com.example.demo.repository.ApprovalRepository;
import com.example.demo.repository.TaskRepository;
import com.example.demo.repository.UserProjectRepository;
import com.example.demo.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(rollbackFor = Exception.class)
@RequiredArgsConstructor
public class ApprovalService {

	
	private final TaskRepository taskDao;
	
	private final UserRepository userDao;
	
	private final ApprovalRepository approvalDao;
	
	private final UserProjectRepository userProjectDao;
	
	public ResultMsg submitToggle(int taskId, String ownerId, String comment) throws Exception {
		try {
			var task = taskDao.findById(taskId).get();
			var compFlg = task.isCompletedFlg();
			var submitFlg = task.isSubmitFlg();
			if(submitFlg) {
				if(compFlg) {
					cancel(task, ownerId);
					return ResultMsg.SUBMIT_CANCEL;
				}else {
					submit(task,ownerId,comment);
					return ResultMsg.SUBMIT_SUCCEED;
				}
			}else {
				return ResultMsg.UNKNOWN_ERROR;
			}
		}catch(Exception e) {
			throw e;
		}
		
	}
	
	
	public void submit(Task task, String ownerId, String comment){
		
		task.setCompletedFlg(true);
		taskDao.save(task);
		
		var approver = userDao.findById(ownerId).get();
		var approval = new Approval();
		approval.setTask(task);
		approval.setApprover(approver);
		approval.setAssigneeId(task.getAssignedUser().getUserId());
		approval.setComment(comment);
		approval.setSubmittedAt(LocalDateTime.now());
		approval.setApproverFlg(true);
		approvalDao.save(approval);
		
		var countRecord = userProjectDao.findById(new UserProjectId(ownerId,task.getProjectId())).get();
		countRecord.setUnapprovedCount(countRecord.getUnapprovedCount() + 1);
		userProjectDao.save(countRecord);
	}
	
	
	private void cancel(Task task, String ownerId) {
	
		task.setCompletedFlg(false);
		taskDao.save(task);
		
		var approval = approvalDao.findByTaskAndApproverFlg(task, true).get();
		approval.setApproverFlg(false);
		approvalDao.save(approval);
		
		var countRecord = userProjectDao.findById(new UserProjectId(ownerId,task.getProjectId())).get();
		countRecord.setUnapprovedCount(countRecord.getUnapprovedCount() - 1);
		userProjectDao.save(countRecord);
	}
}
