package com.example.demo.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * プロジェクトテーブルentity
 * 
 * @author shona
 */
@Entity
@Table(name = "projects")
@Data
public class Projects {

	/** プロジェクトID */
	@Id
	@Column(name = "project_id")
	private int projectId;
	
	/** プロジェクト名 */
	private String name;
	
	/** プロジェクト補足 */
	private String discription;
	
	/** 作成日時 */
	@Column(name = "created_at")
	private LocalDateTime cteatedAt;
	
	/** 更新日時 */
	@Column(name = "updated_at")
	private LocalDateTime updatedAt;
}
