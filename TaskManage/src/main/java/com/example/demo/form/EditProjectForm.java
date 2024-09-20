package com.example.demo.form;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class EditProjectForm {

	
	private String projectId;
	
	private String name;

	private String description;
	
	private String title;
	
	private String taskDescription;
	
	private String leaderId;
	
	private LocalDateTime deadline;
}
