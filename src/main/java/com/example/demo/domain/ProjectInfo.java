package com.example.demo.domain;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class ProjectInfo {

	private String projectId;
	
	private String name;
	
	private String description;
	
    private String firstTask;
    
    private String taskDescription;
    
    private LocalDateTime deadline;
    
    private String leaderId;
    
    private String leaderName;
    
    private int progress;
    
    private int members;
	
	private LocalDateTime createdAt;
	
	private LocalDateTime updatedAt;
}
