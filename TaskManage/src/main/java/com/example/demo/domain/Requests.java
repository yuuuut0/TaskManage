package com.example.demo.domain;

import java.util.List;

import com.example.demo.entity.Approval;

import lombok.Data;

@Data
public class Requests {

	private int ParentTaskId;
	
	private String title;
	
	private String ownerId;
	
	private String ownerName;
	
	private List<Approval> approvalList;
}
