package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.service.ApprovalService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/approval")
public class ApprovalController {

	private final ApprovalService approvalService;
	
	@PostMapping(params = "approve")
	public String approve(int approvalId, String reply, boolean approve, RedirectAttributes rs) {
		try {
			var result = approvalService.approve(approvalId, reply, approve);
			rs.addFlashAttribute("alert", result);
		}catch(Exception e) {
			e.printStackTrace();
			rs.addFlashAttribute("error", e.getMessage());
		}
		
		return "redirect:/approval";
	}
	
	
	@PostMapping(params = "read")
	public String read(int approvalId, RedirectAttributes rs) {
		try {
			var result = approvalService.read(approvalId);
			rs.addFlashAttribute("alert", result);
		}catch(Exception e) {
			e.printStackTrace();
			rs.addFlashAttribute("error", e.getMessage());
		}
		
		return "redirect:/approval";
	}
}
