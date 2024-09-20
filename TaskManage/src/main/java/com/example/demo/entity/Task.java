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
import lombok.NoArgsConstructor;

/**
 * タスクテーブルentity
 * 
 * @author shona
 */
@Entity
@Table(name = "tasks")
@Data
@NoArgsConstructor
public class Task {

	/** タスクID */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "task_id")
	private int taskId;
	
	/** todo */
	@NotBlank
	@Size(max = 50)
	private String title;
	
	/** todo補足 */
	@Size(max = 300)
	private String description;
	
	/** 親タスク */
	@Column(name = "parent_id")
	private Integer parentId;
	
	/** 担当ユーザー */
	@ManyToOne
	@JoinColumn(name = "assigned_user_id")
	private UserInfo assignedUser;
	
	/** 完了済みのサブタスク数 */
	@Column(name = "sub_completed")
	private int subCompleted;
	
	/** サブタスク数 */
	@Column(name = "sub_total")
	private int subTotal;
	
	/** 状態 */
	@Column(name = "completed_frg")
	private boolean completedFrg;
	
	/** 優先度 */
	private byte priority;
	
	/** 締め切り */
	private LocalDateTime deadline;
	
	/** プロジェクトID */
	@Column(name = "project_id")
	private String projectId;
	
	/** 作成日時 */
	@Column(name = "created_at")
	private LocalDateTime createdAt;
	
	/** 更新日時 */
	@Column(name = "updated_at")
	private LocalDateTime updatedAt;
	
	/** 完了日時 */
	@Column(name = "completed_at")
	private LocalDateTime completedAt;
	
	
	public Task(int taskId) {
		this.taskId = taskId;
	}
}
