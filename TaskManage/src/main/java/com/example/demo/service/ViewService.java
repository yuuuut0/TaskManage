package com.example.demo.service;

import java.util.ArrayList;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.example.demo.domain.NestingTaskDto;
import com.example.demo.domain.ProjectInfo;
import com.example.demo.dto.BaseDto;
import com.example.demo.dto.MyTaskDto;
import com.example.demo.entity.Task;
import com.example.demo.repository.ProjectRepository;
import com.example.demo.repository.TaskRepository;
import com.example.demo.repository.UserProjectRepository;
import com.example.demo.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ViewService {

	private final ModelMapper mapper;
	
	private final UserRepository userDao;
	
	private final TaskRepository taskDao;
	
	private final ProjectRepository projectDao;
	
	private final UserProjectRepository userProjectDao;
	
	public BaseDto getBaseDto(String userId) {
		var baseDto = new BaseDto();
		
		var userOpt = userDao.findById(userId);
		var user = userOpt.get();
		baseDto.setUser(user);
		
		if(user.getProjectId() != null) {
			var nowProjectOpt = projectDao.findById(user.getProjectId());
			var nowProject = nowProjectOpt.get();
			var nowProjectInfo = mapper.map(nowProject, ProjectInfo.class);
			var firstTask = nowProject.getFirstTask();
			nowProjectInfo.setFirstTask(firstTask.getTitle());
			nowProjectInfo.setTaskDescription(firstTask.getDescription());
			nowProjectInfo.setDeadline(firstTask.getDeadline());
			nowProjectInfo.setLeaderId(firstTask.getAssignedUser().getUserId());
			nowProjectInfo.setLeaderName(firstTask.getAssignedUser().getUsername());
			int progress;
			if(firstTask.isCompletedFrg()) {
				progress = 100;
			}else if(firstTask.getSubTotal() == 0) {
				progress = 0;
			}else {
				progress = (int)(((double)firstTask.getSubCompleted() / firstTask.getSubTotal()) *100);
			}
			nowProjectInfo.setProgress(progress);
			var memberList = userProjectDao.findMemberListByProjectId(nowProject.getProjectId());
			nowProjectInfo.setMembers(memberList.size());
			baseDto.setMemberList(memberList);
			baseDto.setNowProjectInfo(nowProjectInfo);
			
			var parentIdList = taskDao.findParentTaskIdList(userId, nowProject.getProjectId());
			var parentTaskList = taskDao.findAllById(parentIdList);
			if(nowProjectInfo.getLeaderId().equals(userId) && parentTaskList.isEmpty()) {
				parentTaskList = new ArrayList<Task>(parentTaskList);
				parentTaskList.addFirst(firstTask);
			}
			baseDto.setParentTaskLabel(parentTaskList);			
		}
		
		var joinedProjectList = userProjectDao.findProjectsByUserId(userId);
		baseDto.setJoinedProjectList(joinedProjectList);
		
		
		return baseDto;
	}
	
	public MyTaskDto getMyTaskDto(String userId) {
		
		var baseDto = this.getBaseDto(userId);
		if(baseDto.getNowProjectInfo() == null) {
			return null;
		}
		
		var parentTaskList = baseDto.getParentTaskLabel();
		var parentTaskDtoList = new ArrayList<NestingTaskDto>();
		for(Task parentTask : parentTaskList) {
			var myTasks = taskDao.findAllByParentIdAndAssignedUser(parentTask.getTaskId(), baseDto.getUser());
			var myTaskDtoList = new ArrayList<NestingTaskDto>();
			for(Task myTask : myTasks) {
				var myTaskDto = joinSubTask(myTask, 0, 2);
				myTaskDtoList.add(myTaskDto);
			}
			var parentTaskDto = new NestingTaskDto();
			parentTaskDto.setTask(parentTask);
			parentTaskDto.setSubTaskList(myTaskDtoList);
			parentTaskDtoList.add(parentTaskDto);
		}
		
		var myTaskDto = new MyTaskDto();
		myTaskDto.setBaseDto(baseDto);
		myTaskDto.setParentTaskList(parentTaskDtoList);
		
		return myTaskDto;
		
	}
	
	private NestingTaskDto joinSubTask(Task task, int depth, int limit){
		
		var nestingTask = new NestingTaskDto(); 
		nestingTask.setTask(task);
		int progress;
		if(task.isCompletedFrg()) {
			progress = 100;
		}else if(task.getSubTotal() == 0) {
			progress = 0;
		}else {
			progress = (int)(((double)task.getSubCompleted() / task.getSubTotal())*100);
		}
		nestingTask.setProgress(progress);
		
		var completed = false;
		var fakeCompleted = false;
		if(task.isCompletedFrg()) {
			if(task.getSubCompleted() == task.getSubTotal()) {
				completed = true;
			}else{
				fakeCompleted = true;
			}
		}
		nestingTask.setCompleted(completed);
		nestingTask.setFakeCompleted(fakeCompleted);
		
		
		var subTaskDtoList = new ArrayList<NestingTaskDto>();
		var subTaskList = taskDao.findSubTasksByParentId(task.getTaskId());
		
		if(depth != limit && !subTaskList.isEmpty()) {
			for(Task subTask : subTaskList) {
				var nestingSubTask = joinSubTask(subTask, depth + 1, limit);
				if(nestingSubTask != null) {
					subTaskDtoList.add(nestingSubTask);
				}
			}
		}
		nestingTask.setSubTaskList(subTaskDtoList);
		
		return nestingTask;
		
	}
}
