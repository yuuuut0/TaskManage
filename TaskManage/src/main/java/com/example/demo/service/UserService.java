package com.example.demo.service;

import java.time.LocalDateTime;

import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.constant.ResultMsg;
import com.example.demo.entity.Approval;
import com.example.demo.entity.Project;
import com.example.demo.entity.Task;
import com.example.demo.entity.UserInfo;
import com.example.demo.entity.UserProjectId;
import com.example.demo.form.SignupForm;
import com.example.demo.repository.ApprovalRepository;
import com.example.demo.repository.ProjectRepository;
import com.example.demo.repository.TaskRepository;
import com.example.demo.repository.UserProjectRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.util.UtilToggle;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

	/** ModelMapper */
	private final ModelMapper mapper;
	
	/** PasswordEncoder */
	private final PasswordEncoder passwordEncoder;
	
	
	private final UserRepository userDao;
	
	private final UserProjectRepository userProjectDao;
	
	private final TaskRepository taskDao;
	
	private final ProjectRepository projectDao;
	
	private final ApprovalRepository approvalDao;
	
	private final ProjectService projectService;
	
	private final UtilToggle util;
	
	/**
	 * 入力されたユーザー情報をuserinfoテーブルへ保存します。
	 * 
	 * @param dto signup画面のform情報
	 * @return 登録結果
	 */
	public ResultMsg registUserInfo(SignupForm dto) {
		var userInfoExistedOpt = userDao.findById(dto.getUserId());
		if(userInfoExistedOpt.isPresent()) {
			return ResultMsg.EXISTED_USER_ID;
		}
		
		dto.setPassword(passwordEncoder.encode(dto.getPassword()));
		var userInfo = mapper.map(dto, UserInfo.class);
		userInfo.setLastActiveAt(LocalDateTime.now());
		userDao.save(userInfo);
		return ResultMsg.SIGNUP_SUCCEED;
	}
	
	
	public ResultMsg changeProject(String userId, String projectId) {
		var id = new UserProjectId(userId, projectId);
		var handleOpt = userProjectDao.findById(id);
		if(handleOpt.isPresent()) {
			var handle = handleOpt.get().getHandle();
			var userInfoOpt = userDao.findById(userId);
			if(userInfoOpt.isPresent()) {
				var userInfo = userInfoOpt.get();
				userInfo.setProjectId(projectId);
				userInfo.setHandle(handle);
				userDao.save(userInfo);
				return ResultMsg.EDIT_SUCCEED;
			}
		}
		return ResultMsg.UNKNOWN_ERROR;
	}
	
	
	public ResultMsg setName(String userId, String newName) {
		var user = userDao.findById(userId).get();
		user.setUsername(newName);
		userDao.save(user);
		
		return ResultMsg.EDIT_SUCCEED;
	}
	
	@Transactional(rollbackFor = Exception.class)
	public ResultMsg setHandle(String userId, String handle, String projectId) {
		if(userProjectDao.existsByProjectAndHandle(new Project(projectId), handle)) {
			return ResultMsg.EXISTED_HANDLE;
		}
		
		var userProject = userProjectDao.findById(new UserProjectId(userId, projectId)).get();
		userProject.setHandle(handle);
		userProjectDao.save(userProject);
		
		var user = userDao.findById(userId).get();
		user.setHandle(handle);
		user.setProjectId(projectId);
		userDao.save(user);
		
		return ResultMsg.EDIT_SUCCEED;
	}
	
	
	@Transactional(rollbackFor = Exception.class)
	public ResultMsg exit(String userId, String projectId) {
		var user = userDao.findById(userId).get();
		var project = projectDao.findById(projectId).get();
		
		exitProject(project, user);
		
		userProjectDao.deleteById(new UserProjectId(userId, projectId));
		
		user.setHandle(null);
		user.setProjectId(null);
		userDao.save(user);
		
		return ResultMsg.EDIT_SUCCEED;
	}
	
	
	public void delete(String userId) {
		var user = userDao.findById(userId).get();
		var projects = userProjectDao.findProjectsByUserId(userId);
		for(Project project : projects) {
			exitProject(project, user);
		}
		
		userDao.delete(user);
	}
	
	
	private void exitProject(Project project, UserInfo user) {
		var projectId = project.getProjectId();
		var userId = user.getUserId();
		if(project.getFirstTask().getAssignedUser().getUserId().equals(userId)) {
			projectService.deleteProject(project);
		}else {
			var myTasks = taskDao.findAllByProjectIdAndAssignedUser(projectId, user);
			if(!myTasks.isEmpty()) {
				for(Task task : myTasks) {
					task.setAssignedUser(null);
					task.setUpdatedAt(LocalDateTime.now());
					if(task.isCompletedFlg() && task.isSubmitFlg()) {
						var parentTask = taskDao.findById(task.getParentId()).get();
						util.cancel(task, parentTask.getAssignedUser().getUserId());
					}
				}
				taskDao.saveAll(myTasks);
			}
		}
		
		var approvals = approvalDao.findAllByAssigneeAndAssigneeFlgAndProjectId(user, true, projectId);
		for(Approval approval : approvals) {
			approval.setAssigneeFlg(false);
		}
		approvalDao.saveAll(approvals);
		
	}
	
}
