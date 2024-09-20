package com.example.demo.controller;

import java.time.LocalDateTime;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.WebAttributes;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.demo.form.LoginForm;
import com.example.demo.form.SignupForm;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.UserService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class LoginController {

	/** セッション */
	private final HttpSession session;
	
	/** ユーザー情報repository */
	private final UserRepository userDao;
	
	private final UserService userService;
	
	/**
	 * springSucrityからエラーが出た場合にエラーmsgを持ってログイン画面を表示する
	 * 
	 * @param model
	 * @param form
	 * @return
	 */
	@GetMapping(value = "/login", params = "error")
	public String viewWithError(Model model, LoginForm form) {
		var errorInfo =(Exception)session.getAttribute(WebAttributes.AUTHENTICATION_EXCEPTION) ;
		model.addAttribute("errorMsg", errorInfo.getMessage());
		return "login";
	}
	
	/**
	 * ログイン成功時にユーザーの最終アクセス時間を更新してホーム画面に移る
	 * 
	 * @param userDetails
	 * @return
	 */
	@GetMapping("/loginSuccess")
	public String view(@AuthenticationPrincipal UserDetails userDetails) {
		
		var userId = userDetails.getUsername();
		userDao.updateLastActiveAt(LocalDateTime.now(), userId);
		
		return "redirect:home";
	}
	
	
	@PostMapping("/signup")
	public String signup(Model model, @Validated SignupForm signupForm, BindingResult bdResult) {
		if(bdResult.hasErrors()) {
			model.addAttribute("message", "入力項目にエラーがあります");
			model.addAttribute("isError", true);
			return "signup";
		}
		
		var signupResult = userService.registUserInfo(signupForm);
		model.addAttribute("message", signupResult.getMessage());
		model.addAttribute("isError", signupResult.isError());
		
		return "signup";
	}
	
}
