package com.example.demo.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.example.demo.domain.ApprovalInfo;
import com.example.demo.domain.ApprovalRecord;
import com.example.demo.domain.FreeTask;
import com.example.demo.domain.Member;
import com.example.demo.domain.MyStatus;
import com.example.demo.domain.NestingTaskDto;
import com.example.demo.domain.ProjectInfo;
import com.example.demo.domain.TaskAddInfo;
import com.example.demo.dto.ApprovalDto;
import com.example.demo.dto.BaseDto;
import com.example.demo.dto.FreeDto;
import com.example.demo.dto.MyTaskDto;
import com.example.demo.dto.OverviewDto;
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
				parentTaskList.add(0, firstTask);
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
				var TaskDto = joinSubTask(myTask, 0, 2, baseDto.getNowProjectInfo().getProjectId());
				myTaskDtoList.add(TaskDto);
			}
			var parentTaskDto = new NestingTaskDto();
			parentTaskDto.setTask(parentTask);
			parentTaskDto.setSubTaskList(myTaskDtoList);
			parentTaskDtoList.add(parentTaskDto);
		}
		myTaskDto.setParentTaskList(parentTaskDtoList);
		
		var myAllTasks = taskDao.findAllByProjectIdAndAssignedUser(baseDto.getNowProjectInfo().getProjectId(), baseDto.getUser());
		int comp = 0, fake = 0;
		for(Task task : myAllTasks) {
			var compFlg = task.isCompletedFlg();
			var submitFlg = task.isSubmitFlg();
			var subComp = task.getSubCompleted();
			var subTotal = task.getSubTotal();
			if(compFlg) {
				if(submitFlg || !(subTotal == subComp)) {
					fake++;
				}else {
					comp++;
				}
			}else if(submitFlg) {
				if(subTotal == subComp && subTotal != 0) {
					fake++;
				}
			}
		}
		var total = myAllTasks.size();
		int compRate = 0, fakeRate = 0;
		if(total != 0) {
			compRate = comp*100/total;
			fakeRate = fake*100/total;
		}
		var myStatus = new MyStatus(total, comp, fake, compRate, fakeRate);
		myTaskDto.setStatus(myStatus);
		
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
		
		if(log) {
			approverList = approvalDao.findAllByParentTaskAssignedUserAndProjectIdAndFlag(userId, projectId);
			assigneeList = approvalDao.findAllByTaskAssignedUserAndProjectIdAndFlags(userId, projectId);
			
		}else {
			approverList = approvalDao.findAllByApproverAndApproverFlgAndProjectId(baseDto.getUser(), true, projectId);
			assigneeList = approvalDao.findAllByAssigneeAndAssigneeFlgAndProjectId(baseDto.getUser(), true, projectId);
		}
		var unapprovedList = mapToApprovalRecords(approverList);
		approvalDto.setUnapprovedList(unapprovedList);
		var requestsList = mapToApprovalRecords(assigneeList);
		approvalDto.setRequestsList(requestsList);
		
		return approvalDto;
		
	}
	
	private Collection<ApprovalRecord> mapToApprovalRecords(List<Approval> approvals) {
		var taskMap = new HashMap<Integer, ApprovalRecord>();
		var parentIds = new HashSet<Integer>();
		var parentTaskMap = new HashMap<Integer, Task>();
		for(Approval approval : approvals) {
			var approvalInfo = mapper.map(approval, ApprovalInfo.class);
			var taskId = approval.getTask().getTaskId();
			if(!taskMap.containsKey(taskId)) {
				var approvalRecord = new ApprovalRecord();
				approvalRecord.setTask(approval.getTask());
				approvalRecord.setApprovalList(new ArrayList<ApprovalInfo>());
				taskMap.put(taskId, approvalRecord);
				parentIds.add(approval.getTask().getParentId());
			}
			taskMap.get(taskId).getApprovalList().add(approvalInfo);
		}
		if(!taskMap.isEmpty()) {
			var parentTasks = taskDao.findAllById(parentIds);
			for(Task task : parentTasks) {
				parentTaskMap.put(task.getTaskId(), task);
			}
		}
		for(ApprovalRecord record : taskMap.values()) {
			var parentTask = parentTaskMap.get(record.getTask().getParentId());
			record.setOwnerId(parentTask.getAssignedUser().getUserId());
			record.setOwnerName(parentTask.getAssignedUser().getUsername());
			record.setTitle(parentTask.getTitle());
		}
		return taskMap.values();
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
	
	public OverviewDto getOverviewDto(String userId) {
		var overviewDto = new OverviewDto();
		var baseDto = getBaseDto(userId);
		overviewDto.setBaseDto(baseDto);
		if(baseDto.getNowProjectInfo() == null) {
			return overviewDto;
		}
		
		var nowProject = projectDao.findById(baseDto.getUser().getProjectId()).get();
		var rootTask = nowProject.getFirstTask();
		
		var rootTaskDto = joinSubTask(rootTask, 0, Integer.MAX_VALUE, nowProject.getProjectId());
		
		overviewDto.setRootTask(rootTaskDto);
		
		return overviewDto;
	}
	
	
	private NestingTaskDto joinSubTask(Task task, int depth, int limit, String nowProjectId){
		
		var nestingTask = new NestingTaskDto(); 
		nestingTask.setTask(task);
		
		var addInfo = getAddInfo(task);
		mapper.map(addInfo, nestingTask);
		
		var subTaskDtoList = new ArrayList<NestingTaskDto>();
		var subTaskList = taskDao.findAllByParentIdAndProjectId(task.getTaskId(), nowProjectId);
		
		if(depth != limit && !subTaskList.isEmpty()) {
			for(Task subTask : subTaskList) {
				var nestingSubTask = joinSubTask(subTask, depth + 1, limit, nowProjectId);
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
