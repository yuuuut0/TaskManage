package com.example.demo.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.example.demo.domain.FreeTask;
import com.example.demo.domain.Member;
import com.example.demo.domain.NestingTaskDto;
import com.example.demo.domain.ProjectInfo;
import com.example.demo.domain.Requests;
import com.example.demo.domain.TaskAddInfo;
import com.example.demo.domain.Unapproved;
import com.example.demo.dto.ApprovalDto;
import com.example.demo.dto.BaseDto;
import com.example.demo.dto.FreeDto;
import com.example.demo.dto.MyTaskDto;
import com.example.demo.entity.Approval;
import com.example.demo.entity.Task;
import com.example.demo.entity.UserProjectId;
import com.example.demo.repository.ApprovalRepository;
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
	
	private final ApprovalRepository approvalDao;
	
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
			if(firstTask.isCompletedFlg()) {
				progress = 100;
			}else if(firstTask.getSubTotal() == 0) {
				progress = 0;
			}else {
				progress = (int)(((double)firstTask.getSubCompleted() / firstTask.getSubTotal()) *100);
			}
			nowProjectInfo.setProgress(progress);
			
			var memberList = userProjectDao.findMemberListByProjectId(nowProject.getProjectId());
			var memberMap = new HashMap<String, String>();
			for(Member member : memberList) {
				memberMap.put(member.getUserId(), member.getHandle());
			}
			baseDto.setMemberMap(memberMap);
			
			nowProjectInfo.setMembers(memberList.size());
			baseDto.setNowProjectInfo(nowProjectInfo);
			
			var userProject = userProjectDao.findById(new UserProjectId(userId,nowProject.getProjectId())).get();
			var approvalCount = userProject.getUnapprovedCount() + userProject.getRequestsCount();
			baseDto.setApprovalCount(approvalCount);
			
			
			var parentIdList = taskDao.findParentTaskIdList(userId, nowProject.getProjectId());
			var parentTaskList = taskDao.findAllById(parentIdList);
			if(nowProjectInfo.getLeaderId().equals(userId) && !parentTaskList.contains(firstTask)) {
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
		
		var myTaskDto = new MyTaskDto();
		var baseDto = this.getBaseDto(userId);
		myTaskDto.setBaseDto(baseDto);
		if(baseDto.getNowProjectInfo() == null) {
			return myTaskDto;
		}
		
		var parentTaskList = baseDto.getParentTaskLabel();
		var parentTaskDtoList = new ArrayList<NestingTaskDto>();
		for(Task parentTask : parentTaskList) {
			var myTasks = taskDao.findAllByParentIdAndAssignedUser(parentTask.getTaskId(), baseDto.getUser());
			var myTaskDtoList = new ArrayList<NestingTaskDto>();
			for(Task myTask : myTasks) {
				var TaskDto = joinSubTask(myTask, 0, 2);
				myTaskDtoList.add(TaskDto);
			}
			var parentTaskDto = new NestingTaskDto();
			parentTaskDto.setTask(parentTask);
			parentTaskDto.setSubTaskList(myTaskDtoList);
			parentTaskDtoList.add(parentTaskDto);
		}
		
		myTaskDto.setParentTaskList(parentTaskDtoList);
		
		return myTaskDto;
		
	}
	
	
	public ApprovalDto getApprovalDto(String userId, boolean log) {
		
		var approvalDto = new ApprovalDto();
		
		var baseDto = getBaseDto(userId);
		approvalDto.setBaseDto(baseDto);
		if(baseDto.getNowProjectInfo() == null) {
			return approvalDto;
		}
		
		
		var projectId = baseDto.getNowProjectInfo().getProjectId();
		List<Approval> approverList, assigneeList;
		
		
		
		approverList = approvalDao.findAllByApproverAndApproverFlgAndProjectId(baseDto.getUser(), !log, projectId);
		assigneeList = approvalDao.findAllByAssigneeAndAssigneeFlgAndProjectId(baseDto.getUser(), !log, projectId);
		
		
		var taskList = new HashMap<Integer, List<Approval>>();
		for(Approval approval : approverList) {
			var parentId = approval.getTask().getParentId();
			taskList.computeIfAbsent(parentId, k -> new ArrayList<>()).add(approval);
		}
		var unapprovedList = new ArrayList<Unapproved>();
		if(!taskList.isEmpty()) {
			var parentTasks = taskDao.findAllById(taskList.keySet());
			for(Task task : parentTasks) {
				var unapproved = new Unapproved();
				unapproved.setParentTaskId(task.getTaskId());
				unapproved.setTitle(task.getTitle());
				unapproved.setOwnerId(task.getAssignedUser().getUserId());
				unapproved.setOwnerName(task.getAssignedUser().getUsername());
				unapproved.setApprovalList(taskList.get(task.getTaskId()));
				unapprovedList.add(unapproved);
			}
		}
		approvalDto.setUnapprovedList(unapprovedList);		
		
		taskList = new HashMap<Integer, List<Approval>>();
		for(Approval approval : assigneeList) {
			var parentId = approval.getTask().getParentId();
			taskList.computeIfAbsent(parentId, k -> new ArrayList<>()).add(approval);
		}
		var requestsList = new ArrayList<Requests>();
		if(!taskList.isEmpty()) {
			var parentTasks = taskDao.findAllById(taskList.keySet());
			for(Task task : parentTasks) {
				var requests = new Requests();
				requests.setParentTaskId(task.getTaskId());
				requests.setTitle(task.getTitle());
				requests.setOwnerId(task.getAssignedUser().getUserId());
				requests.setOwnerName(task.getAssignedUser().getUsername());
				requests.setApprovalList(taskList.get(task.getTaskId()));
				requestsList.add(requests);
			}
		}
		approvalDto.setRequestsList(requestsList);	
		
		return approvalDto;
		
	}
	
	public FreeDto getFreeDto(String userId) {
		var freeDto = new FreeDto();
		var baseDto = getBaseDto(userId);
		freeDto.setBaseDto(baseDto);
		if(baseDto.getNowProjectInfo() == null) {
			return freeDto;
		}
		
		var projectId = baseDto.getNowProjectInfo().getProjectId();
		var taskList = taskDao.findAllByProjectIdAndAssignedUserNull(projectId);
		var freeTaskList = new ArrayList<FreeTask>();
		for(Task task : taskList) {
			Task parentTask;
			if(task.getParentId() == null) {
				parentTask = null;
			}else {
				parentTask = taskDao.findById(task.getParentId()).get();
			}
			var freeTask = new FreeTask(task, parentTask);
			var addInfo = getAddInfo(task);
			mapper.map(addInfo, freeTask);
			freeTaskList.add(freeTask);
		}
		freeDto.setFreeTaskList(freeTaskList);
		
		return freeDto;
		
	}
	
	private NestingTaskDto joinSubTask(Task task, int depth, int limit){
		
		var nestingTask = new NestingTaskDto(); 
		nestingTask.setTask(task);
		
		var addInfo = getAddInfo(task);
		mapper.map(addInfo, nestingTask);
		
		var subTaskDtoList = new ArrayList<NestingTaskDto>();
		var subTaskList = taskDao.findAllByParentId(task.getTaskId());
		
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
	
	private TaskAddInfo getAddInfo(Task task) {
		var addInfo = new TaskAddInfo();
		
		int progress;
		boolean submitFlg = task.isSubmitFlg();
		boolean compFlg = task.isCompletedFlg();
		int subComp = task.getSubCompleted();
		int subTotal = task.getSubTotal();
		if(compFlg) {
			progress = 100;
		}else if(subTotal == 0) {
			progress = 0;
		}else {
			progress = (int)(((double)subComp / subTotal)*100);
		}
		addInfo.setProgress(progress);
		
		
		if(compFlg) {
			if(submitFlg || !(subTotal == subComp)) {
				addInfo.setWarning(true);
			}
		}else if(submitFlg) {
			if(subTotal == subComp) {
				addInfo.setWarning(true);
			}
		}
		
		if(submitFlg && !compFlg) {
			if(subTotal != 0 && subTotal == subComp) {
				addInfo.setNeedNotify(true);
			}
		}
		return addInfo;
	}
}
