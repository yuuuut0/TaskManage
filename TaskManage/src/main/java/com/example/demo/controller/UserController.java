package com.example.demo.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.constant.ResultMsg;
import com.example.demo.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
	
	@PostMapping(params = "set")
	public String setting(@AuthenticationPrincipal User user, String set, String setString, String projectId, RedirectAttributes rs) {
		var userId = user.getUsername();
		ResultMsg result;
		try {
			switch(set) {
			case "username":
				result = userService.setName(userId, setString);
				break;
			case "handle":
				result = userService.setHandle(userId, setString, projectId);
				break;
			default:
				result = ResultMsg.UNKNOWN_ERROR;
			}
			rs.addFlashAttribute("alert", result);
			
		}catch (Exception e) {
			e.printStackTrace();
			rs.addFlashAttribute("error", e.getMessage());
		}
		return "redirect:/setting";
	}
	
	@PostMapping(params = "exit")
	public String exitProject(@AuthenticationPrincipal User user, String projectId, RedirectAttributes rs) {
		var userId = user.getUsername();
		try {
			var result = userService.exit(userId, projectId);
			rs.addFlashAttribute("alert", result);
			
			return "redirect:/newProject";
			
		}catch (Exception e) {
			e.printStackTrace();
			rs.addFlashAttribute("error", e.getMessage());
			return "redirect:/setting";
		}
	}
	
	@PostMapping(params = "delete")
	public String delete(@AuthenticationPrincipal User user, RedirectAttributes rs, HttpServletRequest request, HttpServletResponse response) {
		var userId = user.getUsername();
		try {
			userService.delete(userId);
			
			new SecurityContextLogoutHandler().logout(request, response, SecurityContextHolder.getContext().getAuthentication());
			
			return "redirect:/login";
		}catch (Exception e) {
			e.printStackTrace();
			rs.addFlashAttribute("error", e.getMessage());
			return "redirect:/setting";
		}
	}
	
}
