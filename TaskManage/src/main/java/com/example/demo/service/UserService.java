package com.example.demo.service;

import java.time.LocalDateTime;

import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.constant.ResultMsg;
import com.example.demo.entity.UserInfo;
import com.example.demo.entity.UserProjectId;
import com.example.demo.form.SignupForm;
import com.example.demo.repository.UserProjectRepository;
import com.example.demo.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

	/** ユーザー情報テーブルDAO */
	private final UserRepository userDao;
	
	private final UserProjectRepository userProjectDao;
	
	/** ModelMapper */
	private final ModelMapper mapper;
	
	/** PasswordEncoder */
	private final PasswordEncoder passwordEncoder;
	
	
	
	
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
}
