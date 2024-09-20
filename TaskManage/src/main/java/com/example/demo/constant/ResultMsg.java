package com.example.demo.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResultMsg {

	//ユーザー関連
	SIGNUP_SUCCEED("登録が完了しました", false),
	EXISTED_USER_ID("このuserIDは使用されています", true),
	
	//プロジェクト関連
	EXISTED_PROJECT_ID("このprojectIDは使用されています", true),
	NOT_EXISTED_PROJECT_ID("projectIDが存在しません", true),
	JOIN_FAILED("IDとcodeの組み合わせが異なります", true),
	EXISTED_HANDLE("このハンドルネームは使われています", true),
	
	EDIT_SUCCEED("更新しました。", false),
	UNKNOWN_ERROR("エラー", true);
	
	private String message;
	
	private boolean isError;
}
