package com.example.demo.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * ユーザー情報entity
 * 
 * @author shona
 */
@Entity
@Table(name = "user_info")
@Data
public class UserInfo {

	/** ユーザーID */
	@Id
	@Column(name = "user_id")
	private String userId;
	
	/** ユーザーネーム */
	@Column(name = "user_name")
	private String userName;
	
	/** パスワード */
	private String password;
	
	/** 最終アクティブ時間 */
	@Column(name = "last_active_at")
	private LocalDateTime lastActiveAt;
	
	/** 参画プロジェクトID */
	@Column(name = "project_id")
	private int projectId;
}
