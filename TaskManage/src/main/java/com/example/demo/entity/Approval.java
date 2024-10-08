package com.example.demo.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Entity
@Table(name = "task_approvals")
@Data
public class Approval {

	@Id
	@Column(name = "approval_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int approvalId;

	@ManyToOne
    @JoinColumn(name = "task_id")
    private Task task;

	@ManyToOne
    @JoinColumn(name = "approver_id")
    private UserInfo approver;

	@ManyToOne
    @JoinColumn(name = "assignee_id")
    private UserInfo assignee;

    @Size(max = 80)
    private String comment;

    @Size(max = 80)
    private String reply;

    @Column(name = "submitted_at")
    private LocalDateTime submittedAt;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @Column(name = "approver_flg")
    private boolean approverFlg;

    @Column(name = "assignee_flg")
    private boolean assigneeFlg;

    private boolean result;
    
    @Column(name = "project_id")
    private String projectId;
}
