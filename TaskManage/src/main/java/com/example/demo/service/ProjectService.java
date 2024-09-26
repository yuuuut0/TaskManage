package com.example.demo.service;

import java.time.LocalDateTime;

import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.constant.ResultMsg;
import com.example.demo.entity.Project;
import com.example.demo.entity.Task;
import com.example.demo.entity.UserInfo;
import com.example.demo.entity.UserProject;
import com.example.demo.entity.UserProjectId;
import com.example.demo.form.CreateProjectForm;
import com.example.demo.form.EditProjectForm;
import com.example.demo.form.JoinProjectForm;
import com.example.demo.repository.ProjectRepository;
import com.example.demo.repository.TaskRepository;
import com.example.demo.repository.UserProjectRepository;
import com.example.demo.repository.UserRepository;

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
			projectDao.delete(project);
			if(firstTask.getParentId() != null) {
				taskDao.delete(firstTask);
			}
		}else {
			return ResultMsg.UNKNOWN_ERROR;
		}
		return ResultMsg.EDIT_SUCCEED;
	}
}
