package com.example.demo.service;

import java.time.LocalDateTime;

import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.constant.SignupResult;
import com.example.demo.entity.User;
import com.example.demo.form.SignupForm;
import com.example.demo.repository.UserRepository;

import lombok.RequiredArgsConstructor;

/**
 * サインアップ画面service
 * 
 * @author shona
 */
@Service
@RequiredArgsConstructor
public class SignupService {
	
	/** ユーザー情報テーブルDAO */
	private final UserRepository repository;
	
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
	public SignupResult registUserInfo(SignupForm dto) {
		var userInfoExistedOpt = repository.findById(dto.getUserId());
		if(userInfoExistedOpt.isPresent()) {
			return SignupResult.EXISTED_USER_ID;
		}
		
		dto.setPassword(passwordEncoder.encode(dto.getPassword()));
		var userInfo = mapper.map(dto, User.class);
		userInfo.setLastActiveAt(LocalDateTime.now());
		repository.save(userInfo);
		return SignupResult.SUCCEED;
	}

}
