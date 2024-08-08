package com.example.demo.entity;

import java.time.LocalDateTime;

import org.hibernate.validator.constraints.Length;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * タスクテーブルentity
 * 
 * @author shona
 */
@Entity
@Table(name = "tasks")
@Data
public class Task {

	//TODO 外部キー制約の ON DELETE SET NULL を使用する：
	//データベース側で外部キー制約を設定し、ユーザーが削除されたときに assignedUser を自動的に null にする。
	
	//TODO リレーションによるテーブル同士の連結
	
	
	/** タスクID */
	@Id
	@Column(name = "task_id")
	private int taskId;
	
	/** todo */
	@Length(max = 100)
	private String todo;
	
	/** todo補足 */
	@Length(max = 255)
	private String discliption;
	
	/** 親タスク */
	@Column(name = "parent_id")
	private String parentId;
	
	/** 担当ユーザー */
	@Column(name = "assigned_user_id")
	private String assignedUserId;
	
	/** 管理ユーザー */
	@Column(name = "created_by_user_id")
	private String createdByUserId;
	
	/** 優先度 */
	private int priority;
	
	/** 進捗 */
	private int progress;
	
	/** 締め切り */
	private LocalDateTime deadline;
	
	/** 作成日時 */
	@Column(name = "created_at")
	private LocalDateTime createdAt;
	
	@Column(name = "updated_at")
	private LocalDateTime updatedAt;
}
