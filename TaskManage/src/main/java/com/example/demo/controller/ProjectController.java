package com.example.demo.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.form.CreateProjectForm;
import com.example.demo.form.EditProjectForm;
import com.example.demo.form.JoinProjectForm;
import com.example.demo.service.ProjectService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/project")
public class ProjectController {

	private final ProjectService projectService;
	
	//private final TaskService taskService;
	
	@PostMapping(params = "create")
	public String create(@AuthenticationPrincipal User user, CreateProjectForm createProjectForm, RedirectAttributes rs) {
		var userId = user.getUsername();
		try {
			var result = projectService.create(userId, createProjectForm);
			
			if(result.isError()) {
				rs.addFlashAttribute("createProjectForm", createProjectForm);
				rs.addFlashAttribute("alert", result);
				return "redirect:/newProject";
			}else {
				rs.addFlashAttribute("sidebarAlert", result);
				return "redirect:/home";
			}
			
		}catch(Exception e) {
			e.printStackTrace();
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
				rs.addFlashAttribute("alert", result);
				return "redirect:/newProject";
			}else {
				rs.addFlashAttribute("sidebarAlert", result);
				return "redirect:/home";
			}
			
		}catch(Exception e) {
			e.printStackTrace();
			rs.addFlashAttribute("error", e.getMessage());
			return "redirect:/newProject";
		}
	}
	
	@PostMapping(params = "update")
	public String update(@AuthenticationPrincipal User user, EditProjectForm editProjectForm, RedirectAttributes rs) {
		var userId = user.getUsername();
		try {
			var result = projectService.update(userId, editProjectForm);
			rs.addFlashAttribute("sidebarAlert", result);
		}catch(Exception e) {
			e.printStackTrace();
			rs.addFlashAttribute("error", e.getMessage());
		}
		return "redirect:/home";
	}
	
	@PostMapping(params = "updateCode")
	public String updateCode(String projectId, String projectCode, RedirectAttributes rs) {
		try {
			var result = projectService.updateCode(projectId, projectCode);
			rs.addFlashAttribute("sidebarAlert", result);
		}catch(Exception e) {
			e.printStackTrace();
			rs.addFlashAttribute("error", e.getMessage());
		}
		return "redirect:/home";
	}
	
	@PostMapping(params = "delete")
	public String delete(@AuthenticationPrincipal User user, String delete, RedirectAttributes rs) {
		var userId = user.getUsername();
		try {
			var result = projectService.delete(userId, delete);
			if(result.isError()) {
				rs.addFlashAttribute("sidebarAlert", result);
			}else {
				return "redirect:/newProject";
			}
		}catch(Exception e) {
			e.printStackTrace();
			rs.addFlashAttribute("error", e.getMessage());
		}
		return "redirect:/home";
	}
}
