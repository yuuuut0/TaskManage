package com.example.demo.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.service.UserService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

	private final UserService userService;
	
	
	@GetMapping("/{id}")
	public String change(@PathVariable("id") String projectId, @AuthenticationPrincipal User user, RedirectAttributes rs) {
		var result = userService.changeProject(user.getUsername(), projectId);
		rs.addFlashAttribute("sidebarAlert", result);
		return "redirect:/home";
	}
	
}
