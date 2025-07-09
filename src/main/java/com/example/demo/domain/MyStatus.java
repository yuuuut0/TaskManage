package com.example.demo.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MyStatus {

	private int total;
	
	private int comp;
	
	private int fake;
	
	private int compRate;
	
	private int fakeRate;
}
