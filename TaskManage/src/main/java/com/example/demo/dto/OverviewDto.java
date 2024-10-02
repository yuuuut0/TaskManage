package com.example.demo.dto;

import com.example.demo.domain.NestingTaskDto;

import lombok.Data;

@Data
public class OverviewDto {

	private BaseDto baseDto;
	
	private NestingTaskDto rootTask;
}
