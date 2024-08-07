package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.demo.form.SignupForm;
import com.example.demo.service.SignupService;

import lombok.RequiredArgsConstructor;

/**
 * サインアップ用controller
 */
@Controller
@RequiredArgsConstructor
public class SignupController {
	
	/** サインアップ画面service */
	private final SignupService service;
	
	
	@GetMapping("/signup")
	public String view(Model model, SignupForm form) {
		return "signup";
	}
	
	@PostMapping("/signup")
	public String signup(Model model, @Validated SignupForm form, BindingResult bdResult) {
		if(bdResult.hasErrors()) {
			model.addAttribute("message", "入力項目にエラーがあります");
			model.addAttribute("isError", true);
			return "signup";
		}
		
		var signupResult = service.registUserInfo(form);
		model.addAttribute("message", signupResult.getMessage());
		model.addAttribute("isError", signupResult.isError());
		
		return "signup";
	}

}
