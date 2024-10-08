package com.example.demo.domain;

import lombok.Data;

@Data
public class TaskAddInfo {

	private int progress;
	
	private boolean isWarning;
	
	private boolean needNotify;
}
