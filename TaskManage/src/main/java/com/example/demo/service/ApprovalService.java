package com.example.demo.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.constant.ResultMsg;
import com.example.demo.entity.UserProjectId;
import com.example.demo.repository.ApprovalRepository;
import com.example.demo.repository.TaskRepository;
import com.example.demo.repository.UserProjectRepository;
import com.example.demo.util.UtilToggle;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(rollbackFor = Exception.class)
@RequiredArgsConstructor
public class ApprovalService {

	
	private final TaskRepository taskDao;
	
	private final ApprovalRepository approvalDao;
	
	private final UserProjectRepository userProjectDao;
	
	private final UtilToggle util;
	
	
	public ResultMsg approve(int approvalId, String reply, boolean approve) {
		var approval = approvalDao.findById(approvalId).get();
		if(!approval.isApproverFlg()) {
			return ResultMsg.UNKNOWN_ERROR;
		}
		approval.setReply(reply);
		approval.setApprovedAt(LocalDateTime.now());
		approval.setApproverFlg(false);
		approval.setAssigneeFlg(true);
		approval.setResult(approve);
		approvalDao.save(approval);
		
		var task = approval.getTask();
		if(approve) {
			task.setSubmitFlg(false);
			util.complete(task);
		}else {
			task.setCompletedFlg(false);
			task.setUpdatedAt(LocalDateTime.now());
			taskDao.save(task);
		}
		
		var userProjectId = new UserProjectId(approval.getApprover().getUserId() ,task.getProjectId());
		var countByApprover = userProjectDao.findById(userProjectId).get();
		countByApprover.setUnapprovedCount(countByApprover.getUnapprovedCount() - 1);
		userProjectDao.save(countByApprover);
		
		userProjectId = new UserProjectId(approval.getAssignee().getUserId() ,task.getProjectId());
		var countByAssignee = userProjectDao.findById(userProjectId).get();
		countByAssignee.setRequestsCount(countByAssignee.getRequestsCount() + 1);
		userProjectDao.save(countByAssignee);
		
		return ResultMsg.EDIT_SUCCEED;
	}
	
	public ResultMsg read(int approvalId) {
		var approval = approvalDao.findById(approvalId).get();
		approval.setAssigneeFlg(false);
		approvalDao.save(approval);
		
		var userProjectId = new UserProjectId(approval.getAssignee().getUserId() ,approval.getTask().getProjectId());
		var countByAssignee = userProjectDao.findById(userProjectId).get();
		countByAssignee.setRequestsCount(countByAssignee.getRequestsCount() - 1);
		userProjectDao.save(countByAssignee);
		
		return ResultMsg.EDIT_SUCCEED;
	}
	
	
	@Transactional(rollbackFor = Exception.class)
	public ResultMsg submitToggle(int taskId, String ownerId, String comment){
		try {
			var task = taskDao.findById(taskId).get();
			var compFlg = task.isCompletedFlg();
			var submitFlg = task.isSubmitFlg();
			if(submitFlg) {
				if(compFlg) {
					util.cancel(task, ownerId);
					return ResultMsg.SUBMIT_CANCEL;
				}else {
					util.submit(task,ownerId,comment);
					return ResultMsg.SUBMIT_SUCCEED;
				}
			}else {
				return ResultMsg.UNKNOWN_ERROR;
			}
		}catch(Exception e) {
			throw e;
		}
		
	}
	
	
}
