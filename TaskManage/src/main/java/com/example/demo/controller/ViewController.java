package com.example.demo.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.demo.form.CreateProjectForm;
import com.example.demo.form.CreateTaskForm;
import com.example.demo.form.EditProjectForm;
import com.example.demo.form.EditTaskForm;
import com.example.demo.form.JoinProjectForm;
import com.example.demo.form.LoginForm;
import com.example.demo.form.SignupForm;
import com.example.demo.service.ViewService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ViewController {

	private final ViewService viewService;
	
	@GetMapping("/login")
	public String login(Model model, LoginForm form) {
		return "login";
	}
	
	@GetMapping("/signup")
	public String signup(Model model, SignupForm form) {
		return "signup";
	}
	
	@GetMapping("/home")
	public String home(@AuthenticationPrincipal User user, Model model, HttpServletRequest request,
			EditProjectForm editProjectForm, CreateTaskForm createTaskForm, EditTaskForm editTaskForm) {
		
		var userId = user.getUsername();
		var dto = viewService.getMyTaskDto(userId);
		if(dto.getBaseDto().getNowProjectInfo() == null) {
			return "redirect:/newProject";
		}
		
		String uri = request.getRequestURI();
		
		model.addAttribute("user", dto.getBaseDto().getUser());
		model.addAttribute("nowProjectInfo", dto.getBaseDto().getNowProjectInfo());
		model.addAttribute("joinedProjectList", dto.getBaseDto().getJoinedProjectList());
		model.addAttribute("memberMap", dto.getBaseDto().getMemberMap());
		model.addAttribute("parentTaskLabel", dto.getBaseDto().getParentTaskLabel());
		model.addAttribute("approvalCount", dto.getBaseDto().getApprovalCount());
		model.addAttribute("parentTaskList", dto.getParentTaskList());
		model.addAttribute("status", dto.getStatus());
		model.addAttribute("uri", uri);
		
		return "home";
	}
	
	
	@GetMapping("/overview")
	public String overview(@AuthenticationPrincipal User user, Model model, HttpServletRequest request,
			EditProjectForm editProjectForm, EditTaskForm editTaskForm) {
		
		var userId = user.getUsername();
		var dto = viewService.getOverviewDto(userId);
		if(dto.getBaseDto().getNowProjectInfo() == null) {
			return "redirect:/newProject";
		}
		
		String uri = request.getRequestURI();
		
		model.addAttribute("user", dto.getBaseDto().getUser());
		model.addAttribute("nowProjectInfo", dto.getBaseDto().getNowProjectInfo());
		model.addAttribute("joinedProjectList", dto.getBaseDto().getJoinedProjectList());
		model.addAttribute("memberMap", dto.getBaseDto().getMemberMap());
		model.addAttribute("parentTaskLabel", dto.getBaseDto().getParentTaskLabel());
		model.addAttribute("approvalCount", dto.getBaseDto().getApprovalCount());
		model.addAttribute("rootTask", dto.getRootTask());
		model.addAttribute("uri", uri);
		
		return "overview";
	}
	
	
	@GetMapping("/approval")
	public String approval(@AuthenticationPrincipal User user, Model model, boolean log, HttpServletRequest request,
			EditProjectForm editProjectForm, EditTaskForm editTaskForm) {
		
		var userId = user.getUsername();
		var dto = viewService.getApprovalDto(userId, log);
		if(dto.getBaseDto().getNowProjectInfo() == null) {
			return "redirect:/newProject";
		}
		
		String uri = request.getRequestURI();
		
		model.addAttribute("user", dto.getBaseDto().getUser());
		model.addAttribute("nowProjectInfo", dto.getBaseDto().getNowProjectInfo());
		model.addAttribute("memberMap", dto.getBaseDto().getMemberMap());
		model.addAttribute("joinedProjectList", dto.getBaseDto().getJoinedProjectList());
		model.addAttribute("approvalCount", dto.getBaseDto().getApprovalCount());
		model.addAttribute("unapprovedList", dto.getUnapprovedList());
		model.addAttribute("requestsList", dto.getRequestsList());
		model.addAttribute("log", log);
		model.addAttribute("uri", uri);
		
		return "approval";
	}
	
	
	@GetMapping("/free")
	public String free(@AuthenticationPrincipal User user, Model model, HttpServletRequest request,
			EditProjectForm editProjectForm, EditTaskForm editTaskForm) {
		
		var userId = user.getUsername();
		var dto = viewService.getFreeDto(userId);
		if(dto.getBaseDto().getNowProjectInfo() == null) {
			return "redirect:/newProject";
		}
		
		String uri = request.getRequestURI();
		
		model.addAttribute("user", dto.getBaseDto().getUser());
		model.addAttribute("nowProjectInfo", dto.getBaseDto().getNowProjectInfo());
		model.addAttribute("memberMap", dto.getBaseDto().getMemberMap());
		model.addAttribute("joinedProjectList", dto.getBaseDto().getJoinedProjectList());
		model.addAttribute("approvalCount", dto.getBaseDto().getApprovalCount());
		model.addAttribute("freeTaskList", dto.getFreeTaskList());
		model.addAttribute("uri", uri);
		
		return "free";
	}
	
	
	
	
	@GetMapping("/setting")
	public String setting(@AuthenticationPrincipal User user, Model model, EditProjectForm editProjectForm) {
		
		var userId = user.getUsername();
		var dto = viewService.getBaseDto(userId);
		
		model.addAttribute("user", dto.getUser());
		model.addAttribute("nowProjectInfo", dto.getNowProjectInfo());
		model.addAttribute("memberMap", dto.getMemberMap());
		model.addAttribute("joinedProjectList", dto.getJoinedProjectList());
		model.addAttribute("approvalCount", dto.getApprovalCount());
		
		return "setting";
	}
	
	@GetMapping("/newProject")
	public String newProject(@AuthenticationPrincipal User user, Model model, 
			EditProjectForm editProjectForm, CreateProjectForm createProjectForm, JoinProjectForm joinProjectForm) {
		
		var userId = user.getUsername();
		var dto = viewService.getBaseDto(userId);
		
		model.addAttribute("user", dto.getUser());
		model.addAttribute("nowProjectInfo", dto.getNowProjectInfo());
		model.addAttribute("memberMap", dto.getMemberMap());
		model.addAttribute("joinedProjectList", dto.getJoinedProjectList());
		model.addAttribute("approvalCount", dto.getApprovalCount());
		
		return "newProject";
	}
	
	
	@GetMapping("/connectProject")
	public String newProject(@AuthenticationPrincipal User user, Model model, EditProjectForm editProjectForm,
			String taskId, String title, String connect) {
		
		var userId = user.getUsername();
		var dto = viewService.getBaseDto(userId);
		
		model.addAttribute("user", dto.getUser());
		model.addAttribute("nowProjectInfo", dto.getNowProjectInfo());
		model.addAttribute("memberMap", dto.getMemberMap());
		model.addAttribute("joinedProjectList", dto.getJoinedProjectList());
		model.addAttribute("approvalCount", dto.getApprovalCount());
		model.addAttribute("taskId", taskId);
		model.addAttribute("title", title);
		model.addAttribute("connect", connect);
		
		return "connectProject";
	}

}
