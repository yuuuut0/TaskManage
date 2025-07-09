package com.example.demo.event;

import java.util.List;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.entity.Approval;
import com.example.demo.entity.UserProjectId;
import com.example.demo.repository.ApprovalRepository;
import com.example.demo.repository.TaskRepository;
import com.example.demo.repository.UserProjectRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TaskRemoval {

	private final ApprovalRepository approvalDao;

    private final UserProjectRepository userProjectDao;
    
    private final TaskRepository taskDao;
	
    @EventListener
    @Transactional(rollbackFor = Exception.class)
	public void handleTaskRemoval(TaskRemovedEvent event) {
    	var task = event.getTask();
		List<Approval> all = approvalDao.findAllByTask(task);
        var approvalOpt = approvalDao.findByTaskAndApproverFlg(task,true);
        var requestOpt = approvalDao.findByTaskAndAssigneeFlg(task,true);
        var projectId = task.getProjectId();
        
        if(approvalOpt.isPresent()) {
        	var approval = approvalOpt.get();
        	var approverId = approval.getApprover().getUserId();
        	var approver = userProjectDao.findById(new UserProjectId(approverId,projectId)).get();
        	approver.setUnapprovedCount(approver.getUnapprovedCount() - 1);
        	userProjectDao.save(approver);
        }
        
        if(requestOpt.isPresent()) {
        	var request = requestOpt.get();
        	var assigneeId = request.getAssignee().getUserId();
        	var assignee = userProjectDao.findById(new UserProjectId(assigneeId, projectId)).get();
        	assignee.setRequestsCount(assignee.getRequestsCount() - 1);
        	userProjectDao.save(assignee);
        }
        
        approvalDao.deleteAll(all);
        
        var subTasks = taskDao.findAllByParentIdAndProjectId(task.getTaskId(), task.getProjectId());
        if(!subTasks.isEmpty()) {
        	taskDao.deleteAll(subTasks);
        }
	}
}
