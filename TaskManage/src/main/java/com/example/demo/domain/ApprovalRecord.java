package com.example.demo.domain;

import java.util.List;

import com.example.demo.entity.Task;

import lombok.Data;

@Data
public class ApprovalRecord {

	private String title;
	
	private String ownerId;
	
	private String ownerName;
	
	private Task task;
	
	private List<ApprovalInfo> approvalList;
}
