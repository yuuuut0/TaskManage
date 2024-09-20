package com.example.demo.service;

import java.time.LocalDateTime;

import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.constant.ResultMsg;
import com.example.demo.entity.Project;
import com.example.demo.entity.Task;
import com.example.demo.entity.UserProject;
import com.example.demo.entity.UserProjectId;
import com.example.demo.form.CreateProjectForm;
import com.example.demo.form.JoinProjectForm;
import com.example.demo.repository.ProjectRepository;
import com.example.demo.repository.TaskRepository;
import com.example.demo.repository.UserProjectRepository;
import com.example.demo.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
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
	
	@Transactional
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
		project.setMembers(1);
		project.setCreatedAt(LocalDateTime.now());
		project.setUpdatedAt(LocalDateTime.now());
		
		try {
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
			
		}catch(Exception e) {
			throw e;
		}
		
		return ResultMsg.EDIT_SUCCEED;
	}
	
	@Transactional
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
		project.setMembers(project.getMembers() + 1);
		
		var userProjectId = new UserProjectId(userId, projectId);
		var userProject = new UserProject(userProjectId, user, project, handle);
		
		try {
			userProjectDao.save(userProject);
			userDao.save(user);
			projectDao.save(project);
			
		}catch(Exception e) {
			throw e;
		}
		
		return ResultMsg.EDIT_SUCCEED;
		
	}
}
