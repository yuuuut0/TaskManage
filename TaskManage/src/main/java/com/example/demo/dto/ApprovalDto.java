package com.example.demo.dto;

import java.util.Collection;

import com.example.demo.domain.ApprovalRecord;

import lombok.Data;

@Data
public class ApprovalDto {

	private BaseDto baseDto;
	
	private Collection<ApprovalRecord> unapprovedList;
	
	private Collection<ApprovalRecord> requestsList;

}
