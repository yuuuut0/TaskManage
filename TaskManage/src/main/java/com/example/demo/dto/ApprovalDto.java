package com.example.demo.dto;

import java.util.List;

import com.example.demo.domain.Requests;
import com.example.demo.domain.Unapproved;

import lombok.Data;

@Data
public class ApprovalDto {

	private BaseDto baseDto;
	
	private List<Unapproved> unapprovedList;
	
	private List<Requests> requestsList;

}
