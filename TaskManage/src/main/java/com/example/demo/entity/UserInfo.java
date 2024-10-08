package com.example.demo.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ユーザー情報entity
 * 
 * @author shona
 */
@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
public class UserInfo {

	/** ユーザーID */
	@Id
	@Size(max = 30)
	@Pattern(regexp = "^[a-zA-z0-9._-]+$")
	@Column(name = "user_id")
	private String userId;
	
	/** ユーザーネーム */
	@NotBlank
	@Size(max = 30)
	private String username;
	
	/** ハンドルネーム */
	@Size(max = 4)
	private String handle;
	
	/** パスワード */
	private String password;
	
	/** 最終アクティブ時間 */
	@Column(name = "last_active_at")
	private LocalDateTime lastActiveAt;
	
	/** 参画プロジェクトID */
	@Column(name = "project_id")
	private String projectId;
	
	
	public UserInfo(String userId) {
		this.userId = userId;
	}
}
