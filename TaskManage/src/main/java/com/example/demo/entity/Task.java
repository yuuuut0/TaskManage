package com.example.demo.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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

	/** タスクID */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "task_id")
	private int taskId;
	
	/** todo */
	@NotBlank
	@Size(max = 30)
	private String title;
	
	/** todo補足 */
	@Size(max = 300)
	private String descliption;
	
	/** 親タスク */
	@Column(name = "parent_id")
	private String parentId;
	
	/** 担当ユーザー */
	@ManyToOne
	@JoinColumn(name = "assigned_user_id")
	private User assignedUser;
	
	/** 完了済みのサブタスク数 */
	@Column(name = "sub_completed")
	private int subCompleted;
	
	/** サブタスク数 */
	@Column(name = "sub_total")
	private int subTotal;
	
	/** 状態 */
	private boolean state;
	
	/** 優先度 */
	private int priority;
	
	/** 締め切り */
	private LocalDateTime deadline;
	
	/** プロジェクトID */
	@ManyToOne
	@JoinColumn(name = "project_id")
	private Project project;
	
	/** 作成日時 */
	@Column(name = "created_at")
	private LocalDateTime createdAt;
	
	/** 更新日時 */
	@Column(name = "updated_at")
	private LocalDateTime updatedAt;
	
	/** 完了日時 */
	@Column(name = "completed_at")
	private LocalDateTime completedAt;
}
