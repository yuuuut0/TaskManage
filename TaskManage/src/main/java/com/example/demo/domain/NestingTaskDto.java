package com.example.demo.domain;

import java.util.List;

import com.example.demo.entity.Task;

import lombok.Data;

@Data
public class NestingTaskDto {

	private Task task;
	
	private int progress;
	
	private boolean completed;
	
	private boolean fakeCompleted;
	
	private List<NestingTaskDto> subTaskList;
}
