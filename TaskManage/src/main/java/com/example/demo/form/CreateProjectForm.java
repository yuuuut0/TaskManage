package com.example.demo.form;

import java.time.LocalDateTime;

import lombok.Data;

/**
 * プロジェクト新規作成画面form
 * 
 * @author shona
 */
@Data
public class CreateProjectForm {

	private String projectId;
	
	private String projectCode;
	
	private String projectName;
	
	private String projectDescription;
	
	private String handle;
	
	private String firstTask;
	
	private String firstTaskDescription;
	
	private LocalDateTime deadline;
	
}
