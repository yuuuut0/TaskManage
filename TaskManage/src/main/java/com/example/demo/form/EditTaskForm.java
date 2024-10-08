package com.example.demo.form;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class EditTaskForm {

	
	private int taskId;
	
	private String title;

	private String description;
	
	private String assignedUserId;
	
	private boolean submitFlg;

	private int priority;
	
	private LocalDateTime deadline;
}
