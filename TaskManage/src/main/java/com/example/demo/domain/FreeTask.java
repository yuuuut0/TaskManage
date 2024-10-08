package com.example.demo.domain;

import com.example.demo.entity.Task;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class FreeTask {

	private Task task;
	
	private int progress;
	
	private boolean isWarning;
	
	private boolean needNotify;
	
	private Task parentTask;
	
	public FreeTask(Task task, Task parentTask) {
		this.task = task;
		this.parentTask = parentTask;
	}
}
