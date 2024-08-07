package com.example.demo.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SignupResult {

	SUCCEED("登録が完了しました", false),
	
	EXISTED_USER_ID("登録済みのuserIDです", true);
	
	private String message;
	
	private boolean isError;
}
