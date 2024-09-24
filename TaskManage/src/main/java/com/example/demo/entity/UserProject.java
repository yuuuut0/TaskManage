package com.example.demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * ユーザーとプロジェクトの中間テーブルentity
 * 
 * @author shona
 */
@Entity
@Table(name = "user_projects")
@Getter
@Setter
@NoArgsConstructor
public class UserProject {

	@EmbeddedId
    private UserProjectId id;

    @ManyToOne
    @MapsId("userId")  // 複合キーの userId にマッピング
    @JoinColumn(name = "user_id")
    private UserInfo userInfo;

    @ManyToOne
    @MapsId("projectId")  // 複合キーの projectId にマッピング
    @JoinColumn(name = "project_id")
    private Project project;

    /** ハンドルネーム */
	@NotBlank
	@Size(max = 4)
	private String handle;
	
	@Column(name = "unapproved_count")
	private int UnapprovedCount;
	
	@Column(name = "requests_count")
	private int RequestsCount;
    
	public UserProject(UserProjectId id, UserInfo user, Project project, String handle) {};
}
