package com.example.demo.dto;

import java.util.List;

import com.example.demo.domain.MyStatus;
import com.example.demo.domain.NestingTaskDto;

import lombok.Data;

@Data
public class MyTaskDto {

	private BaseDto baseDto;
	
	private List<NestingTaskDto> parentTaskList;
	
	private MyStatus status;
}
