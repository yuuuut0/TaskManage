package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.form.CreateTaskForm;
import com.example.demo.form.EditTaskForm;
import com.example.demo.service.ApprovalService;
import com.example.demo.service.TaskService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/task")
public class TaskController {

	private final TaskService taskService;
	
	private final ApprovalService approvalService;
	
	
	@PostMapping(params = "complete")
	public String complete(int taskId, RedirectAttributes rs) {
		try {
			var result = taskService.completeToggle(taskId);
			rs.addFlashAttribute("homeAlert", result);
		}catch(Exception e) {
			e.printStackTrace();
			rs.addFlashAttribute("error", e.getMessage());
		}
		
		return "redirect:/home";
		
	}
	
	@PostMapping(params = "submit")
	public String submit(int taskId, String ownerId, String comment, RedirectAttributes rs) {
		try {
			var result = approvalService.submitToggle(taskId, ownerId, comment);
			rs.addFlashAttribute("homeAlert", result);
		}catch(Exception e) {
			e.printStackTrace();
			rs.addFlashAttribute("error", e.getMessage());
		}
		
		return "redirect:/home";
	}
	
	@PostMapping(params = "delete")
	public String delete(int taskId, RedirectAttributes rs) {
		try {
			var result = taskService.delete(taskId);
			rs.addFlashAttribute("homeAlert", result);
		}catch(Exception e) {
			e.printStackTrace();
			rs.addFlashAttribute("error", e.getMessage());
		}
		
		return "redirect:/home";
	}
	
	@PostMapping(params = "create")
	public String create(CreateTaskForm createTaskForm, RedirectAttributes rs) {
		try {
			var result = taskService.create(createTaskForm);
			rs.addFlashAttribute("homeAlert", result);
		}catch(Exception e) {
			e.printStackTrace();
			rs.addFlashAttribute("error", e.getMessage());
		}
		
		return "redirect:/home";
	}
	
	@PostMapping(params = "update")
	public String edit(EditTaskForm editTaskForm ,RedirectAttributes rs) {
		try {
			var result = taskService.update(editTaskForm);
			rs.addFlashAttribute("homeAlert", result);
		}catch(Exception e) {
			e.printStackTrace();
			rs.addFlashAttribute("error", e.getMessage());
		}
		
		return "redirect:/home";
	}
}
