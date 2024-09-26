package com.example.demo.dto;

import java.util.List;

import com.example.demo.domain.FreeTask;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FreeDto {

	private BaseDto baseDto;
	
	private List<FreeTask> freeTaskList;
}
