package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.demo.form.NewProjectForm;

import lombok.RequiredArgsConstructor;

/**
 * プロジェクト新規作成画面controller
 * 
 * @author shona
 */
@Controller
@RequiredArgsConstructor
public class NewProjectController {

	
	
	
	@GetMapping("/newProject")
	public String view(Model model, NewProjectForm form) {
		
		
		return "newProject";
	}
}
