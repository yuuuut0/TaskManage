package com.example.demo.service;

import java.time.LocalDateTime;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.constant.ResultMsg;
import com.example.demo.entity.Task;
import com.example.demo.entity.UserInfo;
import com.example.demo.form.CreateTaskForm;
import com.example.demo.form.EditTaskForm;
import com.example.demo.repository.TaskRepository;
import com.example.demo.util.UtilToggle;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(rollbackFor = Exception.class)
@RequiredArgsConstructor
public class TaskService {

	
	/** ModelMapper */
	private final ModelMapper mapper;
	
	private final TaskRepository taskDao;
	
	private final UtilToggle util;
	
	public ResultMsg completeToggle(int taskId) {
		try {
			var task = taskDao.findById(taskId).get();
			var compFlg = task.isCompletedFlg();
			var submitFlg = task.isSubmitFlg();
			if(submitFlg) {
				return ResultMsg.UNKNOWN_ERROR;
			}else if(compFlg) {
				if(task.getSubCompleted() == task.getSubTotal() && task.getSubTotal() != 0) {
					return ResultMsg.UNKNOWN_ERROR;
				}else {
					util.incomplete(task);
				}
			}else {
				util.complete(task);
			}
		}catch(Exception e) {
			throw e;
		}
		
		return ResultMsg.EDIT_SUCCEED;
	}
	
	
	public ResultMsg create(CreateTaskForm form) {
		var task = mapper.map(form, Task.class);
		UserInfo user;
		if(form.getAssignedUserId().equals("")) {
			user = null;
		}else {
			user = new UserInfo(form.getAssignedUserId());
		}
		task.setAssignedUser(user);
		task.setCreatedAt(LocalDateTime.now());
		task.setUpdatedAt(LocalDateTime.now());
		taskDao.save(task);
		
		util.updateParentTaskOnCreate(task);
		
		return ResultMsg.EDIT_SUCCEED;
	}
	
	public ResultMsg delete(int taskId) {
		var task = taskDao.findById(taskId).get();
		var compFrg = task.isCompletedFlg();
		var submitFlg = task.isSubmitFlg();
		
		var parentTask = taskDao.findById(task.getParentId()).get();
		var subComp = parentTask.getSubCompleted();
		var subTotal = parentTask.getSubTotal();
		
		if(compFrg && !submitFlg) {
			subComp --;
			subTotal --;
		}else {
			subTotal --;
		}
		parentTask.setSubCompleted(subComp);
		parentTask.setSubTotal(subTotal);
		parentTask.setUpdatedAt(LocalDateTime.now());
		
		taskDao.delete(task);
		taskDao.save(parentTask);
		if(subComp == subTotal && !parentTask.isCompletedFlg() && !parentTask.isSubmitFlg()) {
			util.complete(parentTask);
		}
		
		return ResultMsg.EDIT_SUCCEED;
	}
	
	
	public ResultMsg update(String userId, String ownerId, EditTaskForm form) {
		var task = taskDao.findById(form.getTaskId()).get();
		if(task.isSubmitFlg() != form.isSubmitFlg()) {
			if(!userId.equals(ownerId)) {
				return ResultMsg.UNKNOWN_ERROR;
			}else if(task.isSubmitFlg() && task.isCompletedFlg()) {
				util.cancel(task, ownerId);
				util.complete(task);
			}else if(!task.isSubmitFlg() && task.isCompletedFlg()) {
				util.incomplete(task);
			}
		}
		mapper.map(form, task);
		if(form.getAssignedUserId().equals("")) {
			task.setAssignedUser(null);
		}else {
			task.setAssignedUser(new UserInfo(form.getAssignedUserId()));
		}
		task.setUpdatedAt(LocalDateTime.now());
		taskDao.save(task);
		return ResultMsg.EDIT_SUCCEED;
	}
	
	
	public ResultMsg getTask(String userId, int taskId) {
		var task = taskDao.findById(taskId).get();
		task.setAssignedUser(new UserInfo(userId));
		task.setUpdatedAt(LocalDateTime.now());
		if(task.isCompletedFlg() && task.isSubmitFlg()) {
			return ResultMsg.UNKNOWN_ERROR;
		}
		taskDao.save(task);
		
		return ResultMsg.EDIT_SUCCEED;
	}
	
	
}
