package com.example.demo.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * プロジェクトテーブルentity
 * 
 * @author shona
 */
@Entity
@Table(name = "projects")
@Data
@NoArgsConstructor
public class Project {

	/** プロジェクトID */
	@Id
	@Size(max = 30)
	@Pattern(regexp = "^[a-zA-z0-9._-]+$")
	@Column(name = "project_id")
	private String projectId;
	
	/** プロジェクトコード */
	private String code;
	
	/** プロジェクト名 */
	@NotBlank
	@Size(max = 50)
	private String name;
	
	/** プロジェクト補足 */
	@Size(max = 300)
	private String description;
	
	/** ファーストタスク */
	@ManyToOne
    @JoinColumn(name = "first_task_id")
    private Task firstTask;
	
	/** 作成日時 */
	@Column(name = "created_at")
	private LocalDateTime createdAt;
	
	/** 更新日時 */
	@Column(name = "updated_at")
	private LocalDateTime updatedAt;
	
	
	public Project(String projectId) {
		this.projectId = projectId;
	}
}
