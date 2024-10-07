package com.example.demo.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.constant.ResultMsg;
import com.example.demo.entity.Approval;
import com.example.demo.entity.Project;
import com.example.demo.entity.Task;
import com.example.demo.entity.UserInfo;
import com.example.demo.entity.UserProject;
import com.example.demo.entity.UserProjectId;
import com.example.demo.form.CreateProjectForm;
import com.example.demo.form.EditProjectForm;
import com.example.demo.form.JoinProjectForm;
import com.example.demo.repository.ApprovalRepository;
import com.example.demo.repository.ProjectRepository;
import com.example.demo.repository.TaskRepository;
import com.example.demo.repository.UserProjectRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.util.UtilToggle;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(rollbackFor = Exception.class)
@RequiredArgsConstructor
public class ProjectService {

	/** ModelMapper */
	private final ModelMapper mapper;
	
	/** PasswordEncoder */
	private final PasswordEncoder passwordEncoder;
	
	private final ProjectRepository projectDao;
	
	private final TaskRepository taskDao;
	
	private final UserRepository userDao;
	
	private final UserProjectRepository userProjectDao;
	
	private final ApprovalRepository approvalDao;
	
	private final UtilToggle util;
	
	public ResultMsg create (String userId, CreateProjectForm form) {
		
		var firstTask = new Task();
		var assignedUser = userDao.findById(userId).get();
		
		firstTask.setTitle(form.getFirstTask());
		firstTask.setDescription(form.getFirstTaskDescription());
		firstTask.setAssignedUser(assignedUser);
		firstTask.setCreatedAt(LocalDateTime.now());
		firstTask.setUpdatedAt(LocalDateTime.now());
		
		var project = mapper.map(form, Project.class);
		var encodedCode = passwordEncoder.encode(form.getProjectCode());
		project.setCode(encodedCode);
		project.setName(form.getProjectName());
		project.setDescription(form.getProjectDescription());
		project.setCreatedAt(LocalDateTime.now());
		project.setUpdatedAt(LocalDateTime.now());
		
		
		var savedTask = taskDao.save(firstTask);
		
		if(!projectDao.existsById(project.getProjectId())) {
			project.setFirstTask(savedTask);
			projectDao.save(project);
		}else {
			return ResultMsg.EXISTED_PROJECT_ID;
		}
		
		var handle = form.getHandle();
		var userProjectId = new UserProjectId(userId, project.getProjectId());
		var userProject = new UserProject(userProjectId, assignedUser, project, handle);
		userProjectDao.save(userProject);
		
		assignedUser.setHandle(handle);
		assignedUser.setProjectId(project.getProjectId());
		userDao.save(assignedUser);
		
		return ResultMsg.EDIT_SUCCEED;
	}
	
	public ResultMsg join(String userId, JoinProjectForm form) {
		var projectId = form.getProjectId();
		var projectCode = form.getProjectCode();
		var handle = form.getHandle();
		
		if(!projectDao.existsById(projectId)) {
			return ResultMsg.NOT_EXISTED_PROJECT_ID;
		}
		var project = projectDao.findById(projectId).get();
		var isMacth = passwordEncoder.matches(projectCode, project.getCode());
		if(!isMacth) {
			return ResultMsg.JOIN_FAILED;
		}
		if(userProjectDao.existsByProjectAndHandle(project, handle)) {
			return ResultMsg.EXISTED_HANDLE;
		}
		var user = userDao.findById(userId).get();
		user.setProjectId(projectId);
		user.setHandle(handle);
		
		var userProjectId = new UserProjectId(userId, projectId);
		var userProject = new UserProject(userProjectId, user, project, handle);
		
		userProjectDao.save(userProject);
		userDao.save(user);
		projectDao.save(project);
		
		return ResultMsg.EDIT_SUCCEED;
		
	}
	
	public ResultMsg update(String userId, EditProjectForm form) {
		var project = projectDao.findById(form.getProjectId()).get();
		if(!project.getFirstTask().getAssignedUser().getUserId().equals(userId)) {
			return ResultMsg.NOT_AUTHORIZED;
		}
		mapper.map(form, project);
		project.setUpdatedAt(LocalDateTime.now());
		
		var task = taskDao.findById(project.getFirstTask().getTaskId()).get();
		var newLeader = new UserInfo(form.getLeaderId());
		task.setTitle(form.getTitle());
		task.setDescription(form.getTaskDescription());
		task.setAssignedUser(newLeader);
		task.setDeadline(form.getDeadline());
		task.setUpdatedAt(LocalDateTime.now());
		
		
		projectDao.save(project);
		taskDao.save(task);
		
		return ResultMsg.EDIT_SUCCEED;
	}
	
	public ResultMsg updateCode(String projectId, String projectCode) {
		var project = projectDao.findById(projectId).get();
		var code = passwordEncoder.encode(projectCode);
		project.setCode(code);
		projectDao.save(project);
		return ResultMsg.EDIT_SUCCEED;
	}
	
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public ResultMsg delete(String userId, String projectId) {
		var project = projectDao.findById(projectId).get();
		var firstTask = project.getFirstTask();
		if(userId.equals(firstTask.getAssignedUser().getUserId())) {
			deleteProject(project);
		}else {
			return ResultMsg.UNKNOWN_ERROR;
		}
		return ResultMsg.EDIT_SUCCEED;
	}
	
	public void deleteProject(Project project) {
		var firstTask = project.getFirstTask();
		projectDao.delete(project);
		if(firstTask.getParentId() != null) {
			taskDao.delete(firstTask);
		}
	}
	
	
	public ResultMsg connectNew (String userId, String nowProjectId, String newProjectId, String projectCode, String name, String description, int taskId) {
		if(projectDao.existsById(newProjectId)) {
			return ResultMsg.EXISTED_PROJECT_ID;
		}
		
		var loginUser = userDao.findById(userId).get();
		var firstTask = taskDao.findById(taskId).get();
		if(!firstTask.getAssignedUser().getUserId().equals(userId)) {
			return ResultMsg.UNKNOWN_ERROR;
		}
		firstTask.setConnectFlg(true);
		taskDao.save(firstTask);
		
		var newProject = new Project();
		var encodedCode = passwordEncoder.encode(projectCode);
		newProject.setProjectId(newProjectId);
		newProject.setCode(encodedCode);
		newProject.setName(name);
		newProject.setDescription(description);
		newProject.setCreatedAt(LocalDateTime.now());
		newProject.setUpdatedAt(LocalDateTime.now());
		newProject.setFirstTask(firstTask);
		newProject = projectDao.save(newProject);
		
		var secondTasks = taskDao.findAllByParentIdAndProjectId(taskId, nowProjectId);
		var loopList = new LinkedList<Task>(secondTasks);
		var tasks = new ArrayList<Task>();
		var users = new ArrayList<UserInfo>();
		while(!loopList.isEmpty()) {
			var task = loopList.pop();
			
			if(!users.contains(task.getAssignedUser())) {
				users.add(task.getAssignedUser());
			}
			task.setProjectId(newProjectId);
			tasks.add(task);
			
			var subTasks = taskDao.findAllByParentIdAndProjectId(task.getTaskId(), nowProjectId);
			for(Task t : subTasks) {
				loopList.push(t);
			}
		}
		taskDao.saveAll(tasks);
		
		if(!users.contains(loginUser)) {
			users.add(loginUser);
		}
		
		var approverCountMap = new HashMap<String, Integer>();
		var assigneeCountMap = new HashMap<String, Integer>();
		var approvals = approvalDao.findAllByTaskInAndProjectId(tasks, nowProjectId);
		for(Approval approval : approvals) {
			if(approval.isApproverFlg()) {
				var approverId = approval.getApprover().getUserId();
				approverCountMap.put(approverId, approverCountMap.getOrDefault(approverId, 0) + 1);
			}
			if(approval.isAssigneeFlg()) {
				var assigneeId = approval.getAssignee().getUserId();
				assigneeCountMap.put(assigneeId, assigneeCountMap.getOrDefault(assigneeId, 0) + 1);
			}
			approval.setProjectId(newProjectId);
		}
		approvals= approvalDao.saveAll(approvals);
		
		var userProjects = userProjectDao.findAllByUserInfoInAndProject(users, new Project(nowProjectId));
		var newUserProjects = new ArrayList<UserProject>();
		for(UserProject userProject : userProjects) {
			var id = userProject.getUserInfo().getUserId();
			if(approverCountMap.containsKey(id)) {
				userProject.setUnapprovedCount(userProject.getUnapprovedCount() - approverCountMap.get(id));
			}
			if(assigneeCountMap.containsKey(id)) {
				userProject.setRequestsCount(userProject.getRequestsCount() - assigneeCountMap.get(id));
			}
			
			var newUserProject = new UserProject();
			newUserProject.setId(new UserProjectId(id, newProjectId));
			newUserProject.setUserInfo(userProject.getUserInfo());
			newUserProject.setProject(newProject);
			newUserProject.setHandle(userProject.getHandle());
			if(approverCountMap.containsKey(id)) {
				newUserProject.setUnapprovedCount(approverCountMap.get(id));
			}else {
				newUserProject.setUnapprovedCount(0);
			}
			
			if(assigneeCountMap.containsKey(id)) {
				newUserProject.setRequestsCount(assigneeCountMap.get(id));
			}else {
				newUserProject.setRequestsCount(0);
			}
			newUserProjects.add(newUserProject);
		}
		userProjectDao.saveAll(userProjects);
		userProjectDao.saveAll(newUserProjects);
		
		
		loginUser.setProjectId(newProjectId);
		userDao.save(loginUser);
		
		return ResultMsg.EDIT_SUCCEED;
	}
	
	
	public ResultMsg connectOld(String userId, String projectId, String projectCode, int taskId) {
		var task = taskDao.findById(taskId).get();
		if(!task.getAssignedUser().getUserId().equals(userId)) {
			return ResultMsg.UNKNOWN_ERROR;
		}
		if(!projectDao.existsById(projectId)) {
			return ResultMsg.NOT_EXISTED_PROJECT_ID;
		}
		var project = projectDao.findById(projectId).get();
		if(!passwordEncoder.matches(projectCode, project.getCode())) {
			return ResultMsg.JOIN_FAILED;
		}
		var firstTask = project.getFirstTask();
		if(!firstTask.getAssignedUser().getUserId().equals(userId)) {
			return ResultMsg.NOT_AUTHORIZED;
		}
		var nextProject = projectDao.findById(task.getProjectId()).get();
		var nextFirstTask = nextProject.getFirstTask();
		while(true) {
			if(nextFirstTask.getTaskId() == firstTask.getTaskId()) {
				return ResultMsg.CONNECT_ERROR_LOOP;
			}
			if(nextFirstTask.getParentId() == null || nextFirstTask.getProjectId() == null) {
				break;
			}
			nextProject = projectDao.findById(nextFirstTask.getProjectId()).get();
			nextFirstTask = nextProject.getFirstTask();
		}
		
		project.setUpdatedAt(LocalDateTime.now());
		projectDao.save(project);
		
		firstTask.setParentId(taskId);
		firstTask.setProjectId(task.getProjectId());
		taskDao.save(firstTask);
		
		task.setConnectFlg(true);
		taskDao.save(task);
		
		util.updateParentTaskOnCreate(firstTask);
		
		return ResultMsg.EDIT_SUCCEED;
	}
	
	
	public ResultMsg disconnect(String userId, int taskId) {
		var task = taskDao.findById(taskId).get();
		if(!task.getAssignedUser().getUserId().equals(userId)) {
			return ResultMsg.UNKNOWN_ERROR;
		}
		
		task.setParentId(null);
		task.setProjectId(null);
		task.setConnectFlg(false);
		taskDao.save(task);
		
		return ResultMsg.EDIT_SUCCEED;
	}
	
	@Transactional
	public ResultMsg merge(String userId, int taskId) {
		var task = taskDao.findById(taskId).get();
		if(!task.getAssignedUser().getUserId().equals(userId)) {
			return ResultMsg.UNKNOWN_ERROR;
		}
		
		task.setConnectFlg(false);
		taskDao.save(task);
		
		var nowProjectId = task.getProjectId(); 
		var nowProject = projectDao.findById(nowProjectId).get();
		var deleteProject = projectDao.findByFirstTask(task).get();
		
		var nowUserProjects = userProjectDao.findAllByProject(nowProject);
		var deleteUserProjects = userProjectDao.findAllByProject(deleteProject);
		var userProjectMap = new HashMap<String, UserProject>();
		for(UserProject up : nowUserProjects) {
			userProjectMap.put(up.getUserInfo().getUserId(), up);
		}
		for(UserProject up : deleteUserProjects) {
			var upUserId = up.getUserInfo().getUserId();
			UserProject newUp;
			if(userProjectMap.containsKey(upUserId)) {
				newUp = userProjectMap.get(upUserId);
				newUp.setUnapprovedCount(newUp.getUnapprovedCount() + up.getUnapprovedCount());
				newUp.setRequestsCount(newUp.getRequestsCount() + up.getRequestsCount());
			}else {
				newUp = new UserProject();
				newUp.setUserInfo(up.getUserInfo());
				newUp.setProject(new Project(nowProjectId));
				var handle = up.getHandle();
				var count = 1;
				while(userProjectDao.existsByProjectAndHandle(nowProject, handle)) {
					if(handle.length() >= 4) {
						handle = count + "?";
						count++;
					}else {
						handle = handle + "?";
					}
				}
				newUp.setHandle(handle);
			}
			userProjectMap.put(upUserId, newUp);
		}
		userProjectDao.saveAll(userProjectMap.values());
		userProjectDao.deleteAll(deleteUserProjects);
		
		
		var approvals = approvalDao.findAllByProjectId(deleteProject.getProjectId());
		for(Approval approval : approvals) {
			approval.setProjectId(nowProjectId);
		}
		if(!approvals.isEmpty()) {
			approvals= approvalDao.saveAll(approvals);
		}
		
		var tasks = taskDao.findAllByProjectId(deleteProject.getProjectId());
		for(Task t : tasks) {
			t.setProjectId(nowProjectId);
		}
		if(!tasks.isEmpty()) {
			taskDao.saveAll(tasks);
		}
		
		var nullTask = new Task();
		nullTask.setTitle("empty");
		nullTask.setCreatedAt(LocalDateTime.now());
		nullTask.setUpdatedAt(LocalDateTime.now());
		nullTask = taskDao.save(nullTask);
		
		deleteProject.setFirstTask(nullTask);
		deleteProject = projectDao.save(deleteProject);
		taskDao.delete(nullTask);
		
		return ResultMsg.EDIT_SUCCEED;
	}
	
}
