package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * ホーム画面コントローラー
 * 
 * @author shona
 */
@Controller
public class HomeController {

	@GetMapping("/home")
	public String view(Model model) {
		return "home";
	}
}
