package com.example.demo.domain;

import java.time.LocalDateTime;

import com.example.demo.entity.UserInfo;

import lombok.Data;

@Data
public class ApprovalInfo {

    private int approvalId;

    private UserInfo approver;

    private UserInfo assignee;

    private String comment;

    private String reply;

    private LocalDateTime submittedAt;

    private LocalDateTime approvedAt;

    private boolean result;
    
}
