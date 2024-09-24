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

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TaskService {

	
	/** ModelMapper */
	private final ModelMapper mapper;
	
	private final TaskRepository taskDao;
	
	
	@Transactional(rollbackFor = Exception.class)
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
					incomplete(task);
				}
			}else {
				complete(task);
			}
		}catch(Exception e) {
			throw e;
		}
		
		return ResultMsg.EDIT_SUCCEED;
	}
	
	@Transactional(rollbackFor = Exception.class)
	private void complete(Task task) {
		try {
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
			
		}catch(Exception e) {
			throw e;
		}
	}
	
	@Transactional(rollbackFor = Exception.class)
	private void incomplete(Task task) {
		try {
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
			
		}catch(Exception e) {
			throw e;
		}
	}
	
	
	public ResultMsg create(CreateTaskForm form) {
		var task = mapper.map(form, Task.class);
		var user = new UserInfo(form.getAssignedUserId());
		task.setAssignedUser(user);
		task.setCreatedAt(LocalDateTime.now());
		task.setUpdatedAt(LocalDateTime.now());
		
		try {
			taskDao.save(task);
			var parentTask = taskDao.findById(task.getParentId()).get();
			parentTask.setCompletedFlg(false);
			parentTask.setSubTotal(parentTask.getSubTotal()+1);
			parentTask.setUpdatedAt(LocalDateTime.now());
			taskDao.save(parentTask);
		}catch(Exception e) {
			throw e;
		}
		
		return ResultMsg.EDIT_SUCCEED;
	}
	
	@Transactional
	public ResultMsg delete(int taskId) {
		var task = taskDao.findById(taskId).get();
		var compFrg = task.isCompletedFlg();
		
		var parentTask = taskDao.findById(task.getParentId()).get();
		var subComp = parentTask.getSubCompleted();
		var subTotal = parentTask.getSubTotal();
		
		if(compFrg) {
			subComp --;
			subTotal --;
		}else {
			subTotal --;
		}
		parentTask.setSubCompleted(subComp);
		parentTask.setSubTotal(subTotal);
		parentTask.setUpdatedAt(LocalDateTime.now());
		
		try {
			taskDao.delete(task);
			taskDao.save(parentTask);
			if(subComp == subTotal && !parentTask.isCompletedFlg()) {
				complete(parentTask);
			}
		}catch(Exception e) {
			throw e;
		}
		
		return ResultMsg.EDIT_SUCCEED;
	}
	
	@Transactional
	public ResultMsg update(EditTaskForm form) {
		var task = taskDao.findById(form.getTaskId()).get();
		mapper.map(form, task);
		task.setAssignedUser(new UserInfo(form.getAssignedUserId()));
		task.setUpdatedAt(LocalDateTime.now());
		taskDao.save(task);
		return ResultMsg.EDIT_SUCCEED;
	}
	
	
}
