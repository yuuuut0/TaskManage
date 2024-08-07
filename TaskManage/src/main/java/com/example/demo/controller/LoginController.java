package com.example.demo.controller;

import org.springframework.security.web.WebAttributes;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.demo.form.LoginForm;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class LoginController {

	/** セッション */
	private final HttpSession session;
	
	@GetMapping("/login")
	public String view(Model model, LoginForm form) {
		return "login";
	}
	
	@GetMapping(value = "/login", params = "error")
	public String viewWithError(Model model, LoginForm form) {
		var errorInfo =(Exception)session.getAttribute(WebAttributes.AUTHENTICATION_EXCEPTION) ;
		model.addAttribute("errorMsg", errorInfo.getMessage());
		return "login";
	}
}
