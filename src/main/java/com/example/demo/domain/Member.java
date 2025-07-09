package com.example.demo.domain;

import lombok.Data;

@Data
public class Member {

	private String userId;
	
	private String handle;
	
	public Member(String userId, String handle) {
		this.userId = userId;
		this.handle = handle;
	}
}
