package com.example.demo.form;

import org.hibernate.validator.constraints.Length;

import lombok.Data;

/**
 * サインアップ画面form
 * 
 * @author shona
 */
@Data
public class SignupForm {

	/** ユーザーID */
	@Length(min = 4, max = 20)
	private String userId;
	
	/** ユーザーネーム */
	@Length(min = 1, max = 20)
	private String username;
	
	/** パスワード */
	@Length(min = 4, max = 20)
	private String password;
	
}
