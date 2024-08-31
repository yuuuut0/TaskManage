package com.example.demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * ユーザーとプロジェクトの中間テーブルentity
 * 
 * @author shona
 */
@Entity
@Table(name = "user_projects")
@Data
public class ProjectByUserId {

	@Id
    @Column(name = "user_id")
    private String UserId;

	@ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;
    
}
