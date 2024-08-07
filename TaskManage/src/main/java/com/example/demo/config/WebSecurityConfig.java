package com.example.demo.config;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.example.demo.authentication.UserDetailsServiceImpl;
import com.example.demo.constant.UrlConst;

import lombok.RequiredArgsConstructor;

/**
 * Spring Securityカスタマイズ用
 * 
 * @author shona
 */
@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
public class WebSecurityConfig {

	/** ユーザー情報生成クラス */
	private final UserDetailsServiceImpl userDetailsService;
	
	/** パスワードエンコーダー */
	private final PasswordEncoder passwordEncoder;
	
	/** メッセージプロパティ */
	private final MessageSource messageSource;
	
	
	/**
	 * Spring Security カスタマイズ用
	 * 
	 * @param http カスタマイズパラメーター
	 * @return カスタマイズ結果
	 * @throws Exception 予期せぬ結果
	 */
	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
		
		http
			.authorizeHttpRequests(authorize -> authorize
				.requestMatchers(UrlConst.NO_AUTHENTICATION).permitAll()
				.anyRequest().authenticated())
		
			.formLogin(
				login -> login.loginPage("/login")
					.usernameParameter("userId")
					.defaultSuccessUrl("/home"))
			
			.logout(logout -> logout.logoutSuccessUrl("/login"));
		
		return http.build();
	}
	
	/**
	 * Provider 定義
	 * 
	 * @return カスタマイズProvider
	 */
	@Bean
	AuthenticationProvider daoAuthenticationProvider() {
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
		provider.setUserDetailsService(userDetailsService);
		provider.setPasswordEncoder(passwordEncoder);
		provider.setMessageSource(messageSource);
		
		return provider;
		
	}
	
	
}
