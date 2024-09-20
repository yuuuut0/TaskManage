package com.example.demo.form;


import java.time.LocalDateTime;

import lombok.Data;


@Data
public class CreateTaskForm {

	private String title;
	
	private String description;
	
	private int parentId;
	
	private String assignedUserId;
	
	private byte priority;
	
	private LocalDateTime deadline;
	
	private String projectId;
	
	
}
