package com.example.demo.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.form.CreateProjectForm;
import com.example.demo.form.JoinProjectForm;
import com.example.demo.service.ProjectService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/project")
public class ProjectControler {

	private final ProjectService projectService;
	
	//private final TaskService taskService;
	
	@PostMapping(params = "create")
	public String create(@AuthenticationPrincipal User user, CreateProjectForm createProjectForm, RedirectAttributes rs) {
		var userId = user.getUsername();
		try {
			var result = projectService.create(userId, createProjectForm);
			
			if(result.isError()) {
				rs.addFlashAttribute("createProjectForm", createProjectForm);
				rs.addFlashAttribute("newProjectAlert", result);
				return "redirect:/newProject";
			}else {
				rs.addFlashAttribute("sidebarAlert", result);
				return "redirect:/home";
			}
			
		}catch(Exception e) {
			rs.addFlashAttribute("error", e.getMessage());
			return "redirect:/newProject";
		}
		
		
	}
	
	@PostMapping(params = "join")
	public String join(@AuthenticationPrincipal User user, JoinProjectForm joinProjectForm, RedirectAttributes rs) {
		try {
			var result = projectService.join(user.getUsername(), joinProjectForm);
			
			if(result.isError()) {
				rs.addFlashAttribute("joinProjectForm", joinProjectForm);
				rs.addFlashAttribute("newProjectAlert", result);
				return "redirect:/newProject";
			}else {
				rs.addFlashAttribute("sidebarAlert", result);
				return "redirect:/home";
			}
			
		}catch(Exception e) {
			rs.addFlashAttribute("error", e.getMessage());
			return "redirect:/newProject";
		}
	}
	
	
}
