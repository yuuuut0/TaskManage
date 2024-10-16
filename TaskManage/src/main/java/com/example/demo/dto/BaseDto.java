package com.example.demo.dto;

import java.util.HashMap;
import java.util.List;

import com.example.demo.domain.ProjectInfo;
import com.example.demo.entity.Project;
import com.example.demo.entity.Task;
import com.example.demo.entity.UserInfo;

import lombok.Data;

@Data
public class BaseDto {

	private UserInfo user;
	
	private ProjectInfo nowProjectInfo;
	
	private HashMap<String, String> memberMap;
	
	private List<Project> joinedProjectList;
	
	private List<Task> parentTaskLabel;
	
	private int approvalCount;
}
