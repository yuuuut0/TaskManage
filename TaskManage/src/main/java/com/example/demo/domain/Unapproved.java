package com.example.demo.domain;

import java.util.List;

import com.example.demo.entity.Approval;

import lombok.Data;

@Data
public class Unapproved {

	private int ParentTaskId;
	
	private String title;
	
	private String ownerId;
	
	private String ownerName;
	
	private List<Approval> approvalList;
}
