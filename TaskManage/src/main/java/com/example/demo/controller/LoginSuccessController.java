package com.example.demo.controller;

import java.time.LocalDateTime;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.demo.repository.UserRepository;

import lombok.RequiredArgsConstructor;


/**
 * ログイン時にユーザーのアクティブ時間を記録する。
 * 
 * @author shona
 */
@Controller
@RequiredArgsConstructor
public class LoginSuccessController {

	/** ユーザー情報repository */
	private final UserRepository repository;
	
	@GetMapping("/loginSuccess")
	public String view(@AuthenticationPrincipal UserDetails userDetails) {
		
		var userId = userDetails.getUsername();
		repository.updareLastActiveAt(LocalDateTime.now(), userId);
		
		return "redirect:home";
	}
	
}
